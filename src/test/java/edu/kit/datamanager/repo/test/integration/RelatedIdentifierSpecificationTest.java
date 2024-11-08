/*
 * Copyright 2024 Karlsruhe Institute of Technology.
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
 * Test specifications for related identifiers. These can be filtered by
 * <ul> <li> value and/or
 * <li> relation type
 * </ul>
 * The setup defines 7 instances. With 2 different relation types and 3
 * different values. One instance is without any relation type.
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

  private static final String UNKNOWN_RELATED_RESOURCE = "something else";
  private static final String RELATED_RESOURCE_1 = "documentLocation";
  private static final String RELATED_RESOURCE_2 = "documentLocation_2";
  private static final String RELATED_RESOURCE_3 = "http://example.org/some%20stupid url";

  private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_1 = RelatedIdentifier.RELATION_TYPES.HAS_METADATA;
  private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_2 = RelatedIdentifier.RELATION_TYPES.IS_METADATA_FOR;
  private static final RelatedIdentifier.RELATION_TYPES RELATION_TYPE_3 = RelatedIdentifier.RELATION_TYPES.IS_DERIVED_FROM;

  @Before
  public void setUp() throws JsonProcessingException {
    dataResourceDao.deleteAll();
    
    createTestResource("hasMetadataResource1", RELATION_TYPE_1, RELATED_RESOURCE_1);
    createTestResource("hasMetadataResource2", RELATION_TYPE_1, RELATED_RESOURCE_2);
    createTestResource("hasMetadataResource3", RELATION_TYPE_1, RELATED_RESOURCE_3);

    createTestResource("isMetadataOfResource1", RELATION_TYPE_2, RELATED_RESOURCE_1);
    createTestResource("isMetadataOfResource2", RELATION_TYPE_2, RELATED_RESOURCE_2);
    createTestResource("isMetadataOfResource3", RELATION_TYPE_2, RELATED_RESOURCE_3);

    createTestResource("noTypeForResource", null, RELATED_RESOURCE_3);
  }

  /**
   * Test specification for related identifier only.
   * @throws Exception  Any error.
   */ 
  @Test
  public void testGetDataResourcesByRelatedResourceAndValue() throws Exception {
    testSingleValue(RELATED_RESOURCE_1, 2);
    testSingleValue(RELATED_RESOURCE_1.toUpperCase(), 0);
    testSingleValue(RELATED_RESOURCE_1.toLowerCase(), 0);
    testSingleValue(RELATED_RESOURCE_2, 2);
    testSingleValue(RELATED_RESOURCE_3, 3);
    testSingleValue(UNKNOWN_RELATED_RESOURCE, 0);
    testSingleValue(null, 0);

    testTwoValues(RELATED_RESOURCE_1, RELATED_RESOURCE_2, 4);
    testTwoValues(RELATED_RESOURCE_1, RELATED_RESOURCE_3, 5);
    testTwoValues(RELATED_RESOURCE_2, RELATED_RESOURCE_3, 5);
    testTwoValues(RELATED_RESOURCE_1.substring(1), RELATED_RESOURCE_3, 3);
    testTwoValues(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_1, 2);
    testTwoValues(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_1, 2);
    testTwoValues(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_2, 2);
    testTwoValues(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_3, 3);
    testTwoValues(UNKNOWN_RELATED_RESOURCE, RELATED_RESOURCE_1.substring(1), 0);

    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(RELATED_RESOURCE_1, RELATED_RESOURCE_2, RELATED_RESOURCE_3);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_2 + " and " + RELATED_RESOURCE_3, 7, findAll.size());
  }

  /**
   * Test specification for no related identifier provided.
   * @throws Exception  Any error.
   */ 
  @Test
  public void testGetDataResourcesByRelatedResourceAndNoValue() throws Exception {
    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(new String[0]);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to new String[0]", 7, findAll.size());

    toSpecification = RelatedIdentifierSpec.toSpecification((String[]) null);
    findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to (String[])null", 7, findAll.size());
  }

  /**
   * Test specification for no relation type only.
   * @throws Exception  Any error.
   */ 
  @Test
  public void testGetDataResourcesByRelatedResourceAndRelatedType() throws Exception {
    testForRelatedType(RELATION_TYPE_1, 3);
    testForRelatedType(RELATION_TYPE_2, 3);
    testForRelatedType(RELATION_TYPE_3, 0);
    testForRelatedType(null, 7);
  }

  /**
   * Test specification for relation type AND related identifier provided.
   * @throws Exception  Any error.
   */ 
  @Test
  public void testGetDataResourcesByRelatedResourceFilteredByValueAndType() throws Exception {
    testForSingleValueAndType(RELATED_RESOURCE_1, RELATION_TYPE_1, 1);
    testForSingleValueAndType(RELATED_RESOURCE_2, RELATION_TYPE_1, 1);
    testForSingleValueAndType(RELATED_RESOURCE_3, RELATION_TYPE_1, 1);

    testForSingleValueAndType(RELATED_RESOURCE_1, RELATION_TYPE_2, 1);
    testForSingleValueAndType(RELATED_RESOURCE_2, RELATION_TYPE_2, 1);
    testForSingleValueAndType(RELATED_RESOURCE_3, RELATION_TYPE_2, 1);

    testForSingleValueAndType(RELATED_RESOURCE_1, RELATION_TYPE_3, 0);
    testForSingleValueAndType(RELATED_RESOURCE_2, RELATION_TYPE_3, 0);
    testForSingleValueAndType(RELATED_RESOURCE_3, RELATION_TYPE_3, 0);

    testForSingleValueAndType(UNKNOWN_RELATED_RESOURCE, RELATION_TYPE_1, 0);

    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(RELATION_TYPE_1, RELATED_RESOURCE_1, RELATED_RESOURCE_3);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to " + RELATED_RESOURCE_1 + " and " + RELATED_RESOURCE_3 + " with type " + RELATION_TYPE_1, 2, findAll.size());
  }

  private DataResource createTestResource(String name, RelatedIdentifier.RELATION_TYPES relationType, String relatedResource) {
    DataResource resource = DataResource.factoryNewDataResource(name);
    resource.setState(DataResource.State.FIXED);
    resource.getRelatedIdentifiers().add(
            RelatedIdentifier.factoryRelatedIdentifier(relationType, relatedResource,
                    Scheme.factoryScheme("id", "uri"), "metadata_scheme")
    );
    return dataResourceDao.save(resource);
  }

  private void testSingleValue(String value, int expectedValue) {
    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(value);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to " + value, expectedValue, findAll.size());
  }

  private void testTwoValues(String value1, String value2, int expectedValue) {
    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(value1, value2);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to " + value1 + " and " + value2, expectedValue, findAll.size());
  }

  private void testForRelatedType(RelatedIdentifier.RELATION_TYPES relationType, int expectedValue) {
    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(relationType);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to with type " + relationType, expectedValue, findAll.size());
  }

  private void testForSingleValueAndType(String value, RelatedIdentifier.RELATION_TYPES relationType, int expectedValue) {
    Specification<DataResource> toSpecification = RelatedIdentifierSpec.toSpecification(relationType, value);
    List<DataResource> findAll = dataResourceDao.findAll(toSpecification);
    Assert.assertEquals("Find all related to " + value + " with type " + relationType, expectedValue, findAll.size());
  }
}
