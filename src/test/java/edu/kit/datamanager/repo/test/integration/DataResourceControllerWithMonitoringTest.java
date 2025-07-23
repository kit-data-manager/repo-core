/*
 * Copyright 2025 Karlsruhe Institute of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.kit.datamanager.repo.test.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.kit.datamanager.entities.Identifier;
import edu.kit.datamanager.entities.PERMISSION;
import edu.kit.datamanager.entities.RepoUserRole;
import edu.kit.datamanager.repo.configuration.RepoBaseConfiguration;
import edu.kit.datamanager.repo.dao.IAllIdentifiersDao;
import edu.kit.datamanager.repo.dao.IContentInformationDao;
import edu.kit.datamanager.repo.dao.IDataResourceDao;
import edu.kit.datamanager.repo.domain.*;
import edu.kit.datamanager.repo.domain.Date;
import edu.kit.datamanager.repo.domain.acl.AclEntry;
import edu.kit.datamanager.repo.service.IDataResourceService;
import edu.kit.datamanager.repo.service.impl.DataResourceService;
import edu.kit.datamanager.service.IAuditService;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.javers.core.Javers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for monitoring endpoint {@link DataResourceController} with monitoring enabled.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class})
@TestPropertySource(properties = {"repo.basepath=file:///tmp/repo_monitoring"})
@TestPropertySource(properties = {"repo.monitoring.enabled=true"})
@TestPropertySource(properties = {"repo.monitoring.serviceName=monitoringtest"})
@TestPropertySource(properties = {"repo.monitoring.noOfDaysToKeep=28"})
@TestPropertySource(properties = {"management.prometheus.metrics.export.enabled=true"})
@TestPropertySource(properties = {"management.endpoints.web.exposure.include=prometheus"})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:db_monitoring;DB_CLOSE_DELAY=-1;MODE=LEGACY;NON_KEYWORDS=VALUE"})
@ActiveProfiles("test")
public class DataResourceControllerWithMonitoringTest {

    public static String KEYCLOAK_SECRET = "SomeReallyVeryVeryLongAndSuperExtraSecureSecret";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IDataResourceDao dataResourceDao;
    @Autowired
    Javers javers = null;
    @Autowired
    private IDataResourceService dataResourceService;
    @Autowired
    private IContentInformationDao contentInformationDao;
    @Autowired
    private IAllIdentifiersDao allIdentifiersDao;

    private IAuditService<ContentInformation> contentInformationAuditService;

    @Autowired
    private RepoBaseConfiguration repositoryConfig;

    private String adminToken;
    private String userToken;
    private String otherUserToken;
    private String guestToken;

    private DataResource sampleResource;
    private DataResource otherResource;
    private DataResource revokedResource;
    private DataResource fixedResource;

    private static Set<Path> allTempFiles = new HashSet<>();

    @Before
    public void setUp() throws JsonProcessingException {
        contentInformationAuditService = repositoryConfig.getContentInformationAuditService();
        contentInformationDao.deleteAll();
        dataResourceDao.deleteAll();
        allIdentifiersDao.deleteAll();

        adminToken = edu.kit.datamanager.util.JwtBuilder.createUserToken("admin", RepoUserRole.ADMINISTRATOR).
                addSimpleClaim("email", "thomas.jejkal@kit.edu").
                addSimpleClaim("orcid", "0000-0003-2804-688X").
                addSimpleClaim("groupid", "USERS").
                addSimpleClaim("loginFailures", 0).
                addSimpleClaim("active", true).
                addSimpleClaim("locked", false).
                getCompactToken(repositoryConfig.getJwtSecret());

        userToken = edu.kit.datamanager.util.JwtBuilder.createUserToken("user", RepoUserRole.USER).
                addSimpleClaim("email", "thomas.jejkal@kit.edu").
                addSimpleClaim("orcid", "0000-0003-2804-688X").
                addSimpleClaim("loginFailures", 0).
                addSimpleClaim("active", true).
                addSimpleClaim("locked", false).
                getCompactToken(repositoryConfig.getJwtSecret());

        otherUserToken = edu.kit.datamanager.util.JwtBuilder.createUserToken("otheruser", RepoUserRole.USER).
                addSimpleClaim("email", "thomas.jejkal@kit.edu").
                addSimpleClaim("orcid", "0000-0003-2804-688X").
                addSimpleClaim("loginFailures", 0).
                addSimpleClaim("active", true).
                addSimpleClaim("locked", false).getCompactToken(repositoryConfig.getJwtSecret());

        guestToken = edu.kit.datamanager.util.JwtBuilder.createUserToken("guest", RepoUserRole.GUEST).
                addSimpleClaim("email", "thomas.jejkal@kit.edu").
                addSimpleClaim("orcid", "0000-0003-2804-688X").
                addSimpleClaim("loginFailures", 0).
                addSimpleClaim("active", true).
                addSimpleClaim("locked", false).getCompactToken(repositoryConfig.getJwtSecret());

        sampleResource = DataResource.factoryNewDataResource("altIdentifier");
        sampleResource.setState(DataResource.State.VOLATILE);
        sampleResource.getDescriptions().add(Description.factoryDescription("This is a description", Description.TYPE.OTHER, "en"));
        sampleResource.getTitles().add(Title.factoryTitle("Title", Title.TYPE.OTHER));
        sampleResource.getCreators().add(Agent.factoryAgent("John", "Doe", new String[]{"KIT"}));
        sampleResource.getCreators().add(Agent.factoryAgent("Johanna", "Doe", new String[]{"FZJ"}));
        sampleResource.getContributors().add(Contributor.factoryContributor(Agent.factoryAgent("Jane", "Doe", new String[]{"KIT"}), Contributor.TYPE.DATA_MANAGER));
        sampleResource.getDates().add(Date.factoryDate(Instant.now().truncatedTo(ChronoUnit.MILLIS), Date.DATE_TYPE.CREATED));
        sampleResource.setEmbargoDate(Instant.now().truncatedTo(ChronoUnit.MILLIS).plus(Duration.ofDays(365)));
        sampleResource.setResourceType(ResourceType.createResourceType("photo", ResourceType.TYPE_GENERAL.IMAGE));
        sampleResource.setLanguage("en");
        sampleResource.setPublisher("me");
        sampleResource.setPublicationYear("2018");
        sampleResource.getFormats().add("plain/text");
        sampleResource.getSizes().add("100");
        sampleResource.getFundingReferences().add(FundingReference.factoryFundingReference("BMBF", FunderIdentifier.factoryIdentifier("BMBF-01", FunderIdentifier.FUNDER_TYPE.ISNI), Scheme.factoryScheme("BMBF_AWARD", "https://www.bmbf.de/"), "https://www.bmbf.de/01", "Award 01"));
        sampleResource.getAcls().add(new AclEntry("admin", PERMISSION.ADMINISTRATE));
        sampleResource.getAcls().add(new AclEntry("otheruser", PERMISSION.READ));
        sampleResource.getAcls().add(new AclEntry("user", PERMISSION.WRITE));
        sampleResource.getGeoLocations().add(GeoLocation.factoryGeoLocation(Point.factoryPoint(12.1f, 13.0f)));
        sampleResource.getGeoLocations().add(GeoLocation.factoryGeoLocation(Box.factoryBox(12.0f, 13.0f, 14.0f, 15.0f)));
        sampleResource.getGeoLocations().add(GeoLocation.factoryGeoLocation(Box.factoryBox(Point.factoryPoint(10.0f, 11.0f), Point.factoryPoint(42.0f, 45.1f))));
        sampleResource.getGeoLocations().add(GeoLocation.factoryGeoLocation(Polygon.factoryPolygon(Point.factoryPoint(12.1f, 13.0f), Point.factoryPoint(14.1f, 12.0f), Point.factoryPoint(16.1f, 11.0f))));
        sampleResource.getGeoLocations().add(GeoLocation.factoryGeoLocation("A place"));
        sampleResource.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RelatedIdentifier.RELATION_TYPES.IS_DOCUMENTED_BY, "document_location", Scheme.factoryScheme("id", "uri"), "metadata_scheme"));
        sampleResource.getSubjects().add(Subject.factorySubject("testing", "uri", "en", Scheme.factoryScheme("id", "uri")));

        sampleResource = dataResourceDao.save(sampleResource);
        ((DataResourceService) dataResourceService).saveIdentifiers(sampleResource);

        otherResource = DataResource.factoryNewDataResource("otherResource");
        otherResource.getDescriptions().add(Description.factoryDescription("This is a description", Description.TYPE.OTHER, "en"));
        otherResource.getTitles().add(Title.factoryTitle("Title", Title.TYPE.OTHER));
        otherResource.getCreators().add(Agent.factoryAgent("John", "Doe", new String[]{"KIT"}));
        otherResource.getDates().add(Date.factoryDate(Instant.now().truncatedTo(ChronoUnit.MILLIS), Date.DATE_TYPE.CREATED));
        otherResource.setPublisher("me");
        otherResource.setPublicationYear("2018");
        otherResource.getAcls().add(new AclEntry("admin", PERMISSION.WRITE));
        otherResource.getAcls().add(new AclEntry("otheruser", PERMISSION.ADMINISTRATE));
        otherResource.getAcls().add(new AclEntry("user", PERMISSION.READ));
        otherResource.setState(DataResource.State.REVOKED);

        otherResource = dataResourceDao.save(otherResource);
        ((DataResourceService) dataResourceService).saveIdentifiers(otherResource);

        revokedResource = DataResource.factoryNewDataResource("revokedResource");
        revokedResource.getDescriptions().add(Description.factoryDescription("This is a description", Description.TYPE.OTHER, "en"));
        revokedResource.getTitles().add(Title.factoryTitle("Title", Title.TYPE.OTHER));
        revokedResource.getCreators().add(Agent.factoryAgent("John", "Doe", new String[]{"KIT"}));
        revokedResource.getDates().add(Date.factoryDate(Instant.now().truncatedTo(ChronoUnit.MILLIS), Date.DATE_TYPE.CREATED));
        revokedResource.setPublisher("me");
        revokedResource.setPublicationYear("2018");
        revokedResource.getAcls().add(new AclEntry("admin", PERMISSION.ADMINISTRATE));
        revokedResource.getAcls().add(new AclEntry("user", PERMISSION.WRITE));
        revokedResource.setState(DataResource.State.REVOKED);

        revokedResource = dataResourceDao.save(revokedResource);
        ((DataResourceService) dataResourceService).saveIdentifiers(revokedResource);

        fixedResource = DataResource.factoryNewDataResource("fixedResource");
        fixedResource.getDescriptions().add(Description.factoryDescription("This is a description", Description.TYPE.OTHER, "en"));
        fixedResource.getTitles().add(Title.factoryTitle("Title", Title.TYPE.OTHER));
        fixedResource.getCreators().add(Agent.factoryAgent("John", "Doe", new String[]{"KIT"}));
        fixedResource.getDates().add(Date.factoryDate(Instant.now().truncatedTo(ChronoUnit.MILLIS), Date.DATE_TYPE.CREATED));
        fixedResource.setPublisher("me");
        fixedResource.setPublicationYear("2018");
        fixedResource.getAcls().add(new AclEntry("admin", PERMISSION.ADMINISTRATE));
        fixedResource.getAcls().add(new AclEntry("user", PERMISSION.WRITE));
        fixedResource.setState(DataResource.State.FIXED);

        fixedResource = dataResourceDao.save(fixedResource);
        ((DataResourceService) dataResourceService).saveIdentifiers(fixedResource);
    }

    @AfterClass
    public static void tearDownClass() {
        DataResourceControllerWithMonitoringTest.deleteAllTempFiles();
    }

    /**
     * FIND TESTS*
     */
    @Test
    public void testGetMetrics() throws Exception {
        this.mockMvc.perform(get("/api/v1/dataresources/").param("page", "0").param("size", "10").header(HttpHeaders.AUTHORIZATION,
                "Bearer " + adminToken)).andDo(print()).andExpect(status().isOk()).andReturn();
        // Now there should be at least 1 unique user
        MvcResult result = this.mockMvc.perform(get("/actuator/prometheus").header(HttpHeaders.AUTHORIZATION,
                "Bearer " + adminToken)).andDo(print()).andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString(Charset.defaultCharset());
        Assert.assertTrue("Metrics should contain unique users", content.contains("monitoringtest_unique_users"));
        Assert.assertTrue("Metrics should contain requests served", content.contains("monitoringtest_requests_served"));
    }
    private static void deleteAllTempFiles() {
        for (Path tempFile : allTempFiles) {
            FileUtils.deleteQuietly(tempFile.toFile());
        }
    }
}
