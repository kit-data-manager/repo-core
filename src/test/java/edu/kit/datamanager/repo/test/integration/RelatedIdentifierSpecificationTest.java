/*
 * Copyright 2018 Karlsruhe Institute of Technology.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.repo.dao.IDataResourceDao;
import edu.kit.datamanager.repo.dao.spec.dataresource.RelatedIdentifierSpec;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.domain.RelatedIdentifier;
import edu.kit.datamanager.repo.domain.Scheme;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

/**
 *
 * @author jejkal
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:related_identifiers;DB_CLOSE_DELAY=-1;MODE=LEGACY;NON_KEYWORDS=VALUE"})
@ActiveProfiles("test")
public class RelatedIdentifierSpecificationTest {
    @Autowired
    private IDataResourceDao dataResourceDao;

    private DataResource hasMetadataResource1;
    private DataResource hasMetadataResource2;
    private DataResource hasMetadataResource3;
    private DataResource isMetadataOfResource1;
    private DataResource isMetadataOfResource2;
    private DataResource isMetadataOfResource3;
    private DataResource resourceWithNoType;
    
    private static final String UNKNOWN_RELATED_RESOURCE = "something else";
    private static final String RELATED_RESOURCE_1 = "document_location";
    private static final String RELATED_RESOURCE_2 = "document_location_2";
    private static final String RELATED_RESOURCE_3 = "document_location_3";
    
    private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_1 = RelatedIdentifier.RELATION_TYPES.HAS_METADATA;
    private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_2 = RelatedIdentifier.RELATION_TYPES.IS_METADATA_FOR;
    private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_3 = RelatedIdentifier.RELATION_TYPES.IS_DERIVED_FROM;

    @Before
    public void setUp() throws JsonProcessingException {
        dataResourceDao.deleteAll();


        hasMetadataResource1 = DataResource.factoryNewDataResource("hasMetadataResource1");
        hasMetadataResource1.setState(DataResource.State.FIXED);
        hasMetadataResource1.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_1, RELATED_RESOURCE_1, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        hasMetadataResource1 = dataResourceDao.save(hasMetadataResource1);

        hasMetadataResource2 = DataResource.factoryNewDataResource("hasMetadataResource2");
        hasMetadataResource2.setState(DataResource.State.FIXED);
        hasMetadataResource2.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_1, RELATED_RESOURCE_2, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        hasMetadataResource2 = dataResourceDao.save(hasMetadataResource2);

        hasMetadataResource3 = DataResource.factoryNewDataResource("hasMetadataResource3");
        hasMetadataResource3.setState(DataResource.State.FIXED);
        hasMetadataResource3.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_1, RELATED_RESOURCE_3, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        hasMetadataResource3 = dataResourceDao.save(hasMetadataResource3);

        isMetadataOfResource1 = DataResource.factoryNewDataResource("isMetadataOfResource1");
        isMetadataOfResource1.setState(DataResource.State.FIXED);
        isMetadataOfResource1.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_2, RELATED_RESOURCE_1, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        isMetadataOfResource1 = dataResourceDao.save(isMetadataOfResource1);

        isMetadataOfResource2 = DataResource.factoryNewDataResource("isMetadataOfResource2");
        isMetadataOfResource2.setState(DataResource.State.FIXED);
        isMetadataOfResource2.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_2, RELATED_RESOURCE_2, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        isMetadataOfResource2 = dataResourceDao.save(isMetadataOfResource2);

        isMetadataOfResource3 = DataResource.factoryNewDataResource("isMetadataOfResource3");
        isMetadataOfResource3.setState(DataResource.State.FIXED);
        isMetadataOfResource3.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(RELATION_TYPE_2, RELATED_RESOURCE_3, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        isMetadataOfResource3 = dataResourceDao.save(isMetadataOfResource3);

        resourceWithNoType = DataResource.factoryNewDataResource("noTypeForResource");
        resourceWithNoType.setState(DataResource.State.FIXED);
        resourceWithNoType.getRelatedIdentifiers().add(RelatedIdentifier.factoryRelatedIdentifier(null, RELATED_RESOURCE_3, Scheme.factoryScheme("id", "uri"), "metadata_scheme"));;
        resourceWithNoType = dataResourceDao.save(resourceWithNoType);
    }

    /**
     * FIND TESTS*
     */
    @Test
    public void testGetDataResourcesByRelatedResourceAndValue() throws Exception {
      Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1);
      List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1, 2, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_2, 2, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_3, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(UNKNOWN_RELATED_RESOURCE);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1, RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_2, 4, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_3, 5, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_2, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_2 + " and " + RELATED_RESOURCE_3, 5, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1.substring(1), RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1.substring(1) + " and " + RELATED_RESOURCE_3, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_1);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE + " and " + RELATED_RESOURCE_1, 2, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE + " and " + RELATED_RESOURCE_2, 2, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE + " and " + RELATED_RESOURCE_3, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1.substring(1), UNKNOWN_RELATED_RESOURCE);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + UNKNOWN_RELATED_RESOURCE, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification((String)null);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to (String)null", 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1, RELATED_RESOURCE_2, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_2 + " and " + RELATED_RESOURCE_3, 7, findAll.size());
    }
    @Test
    public void testGetDataResourcesByRelatedResourceAndNoValue() throws Exception {
      Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(new String[0]);
      List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to new String[0]", 7, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification((String[])null);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to (String[])null" , 7, findAll.size());
      
    }
    @Test
    public void testGetDataResourcesByRelatedResourceAndRelatedType() throws Exception {
      Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1);
      List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to with type " + RELATION_TYPE_1, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to with type " + RELATION_TYPE_2, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to with type " + RELATION_TYPE_3, 0, findAll.size());

      toSpecification = RelatedIdentifierSpec.toSpecification((RelatedIdentifier.RELATION_TYPES)null);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to (RelatedIdentifier.RELATION_TYPES)null)", 7, findAll.size());
    }
    @Test
    public void testGetDataResourcesByRelatedResourceFilteredByValueAndType() throws Exception {
      Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, RELATED_RESOURCE_1);
      List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " with type " + RELATION_TYPE_1, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_2 + " with type " + RELATION_TYPE_1, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_3 + " with type " + RELATION_TYPE_1, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_2, RELATED_RESOURCE_1);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " with type " + RELATION_TYPE_2, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_2, RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_2 + " with type " + RELATION_TYPE_2, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_2, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_3 + " with type " + RELATION_TYPE_2, 1, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_3, RELATED_RESOURCE_1);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " with type " + RELATION_TYPE_2, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_3, RELATED_RESOURCE_2);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_2 + " with type " + RELATION_TYPE_2, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_3, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_3 + " with type " + RELATION_TYPE_2, 0, findAll.size());
       
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, UNKNOWN_RELATED_RESOURCE);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, RELATED_RESOURCE_1, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_3 + " with type " + RELATION_TYPE_1, 2, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1.substring(1), RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 .substring(1)+ " and " + RELATED_RESOURCE_3, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_3);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + UNKNOWN_RELATED_RESOURCE + " and " + RELATED_RESOURCE_3, 3, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1.substring(1), UNKNOWN_RELATED_RESOURCE);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 .substring(1)+ " and " + UNKNOWN_RELATED_RESOURCE, 0, findAll.size());
      
      toSpecification = RelatedIdentifierSpec.toSpecification((String)null);
      findAll = dataResourceDao.findAll(toSpecification);
      Assert.assertEquals("Find all related to null", 0, findAll.size());
    }
}
