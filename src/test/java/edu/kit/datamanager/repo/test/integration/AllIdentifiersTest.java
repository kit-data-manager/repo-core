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
import edu.kit.datamanager.repo.configuration.ApplicationProperties;
import edu.kit.datamanager.repo.dao.IAllIdentifiersDao;
import edu.kit.datamanager.repo.domain.AllIdentifiers;
import edu.kit.datamanager.repo.domain.DataResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

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
@ActiveProfiles("test")
public class AllIdentifiersTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private IAllIdentifiersDao identifiersDao;

  @Autowired
  private ApplicationProperties applicationProperties;
  
  private AllIdentifiers one, two, three, four, five, six;

  @Before
  public void setUp() throws JsonProcessingException {
    AllIdentifiers id = createIdentifier("first", "resource", DataResource.State.GONE);
    one = identifiersDao.save(id);
    id = createIdentifier("second", "resource", DataResource.State.GONE);
    two = identifiersDao.save(id);
    id = createIdentifier("third", "resource", DataResource.State.GONE);
    three = identifiersDao.save(id);
    id = createIdentifier("4", "resource", DataResource.State.REVOKED);
    four = identifiersDao.save(id);
    id = createIdentifier("5", "resource2", DataResource.State.GONE);
    five = identifiersDao.save(id);
    id = createIdentifier("6", "resource", DataResource.State.VOLATILE);
    six = identifiersDao.save(id);
  }

  public AllIdentifiers createIdentifier(String Identifier, String resource, DataResource.State state) {
    AllIdentifiers result = new AllIdentifiers();
    result.setIdentifier(Identifier);
    result.setResourceId(resource);
    result.setStatus(state);
    return result;
  }

  /**
   * FIND TESTS*
   */
  @Test
  public void testAll() throws Exception {
    String[] array = new String[]{"1", "3", "34", "35", "36", "37", "38"};
    List<String> allPossibleIdentifiers = new ArrayList<>();
    Collections.addAll(allPossibleIdentifiers, array);
    Assert.assertEquals(0, identifiersDao.countByIdentifierIn(allPossibleIdentifiers));
    allPossibleIdentifiers.add("4");
    Assert.assertEquals(1, identifiersDao.countByIdentifierIn(allPossibleIdentifiers));
    Assert.assertEquals(0, identifiersDao.countByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.FIXED));
    Assert.assertEquals(1, identifiersDao.countByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.REVOKED));
    Assert.assertEquals(1, identifiersDao.findByIdentifierIn(allPossibleIdentifiers).size());
    Assert.assertTrue(identifiersDao.findByIdentifierIn(allPossibleIdentifiers).contains(four));
     Assert.assertFalse(identifiersDao.findByIdentifierIn(allPossibleIdentifiers).contains(one));
    Assert.assertTrue(identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.VOLATILE).isEmpty());
    Assert.assertTrue(identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.GONE).isEmpty());
    Assert.assertFalse(identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.REVOKED).isEmpty());
    allPossibleIdentifiers.add("first");
    Assert.assertEquals(2, identifiersDao.countByIdentifierIn(allPossibleIdentifiers));
    Assert.assertEquals(0, identifiersDao.countByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.FIXED));
    Assert.assertEquals(1, identifiersDao.countByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.GONE));
    Assert.assertEquals(1, identifiersDao.countByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.REVOKED));
    Assert.assertTrue(identifiersDao.findByIdentifierIn(allPossibleIdentifiers).contains(four));
     Assert.assertTrue(identifiersDao.findByIdentifierIn(allPossibleIdentifiers).contains(one));
  Assert.assertTrue(identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.VOLATILE).isEmpty());
    Assert.assertFalse(identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.REVOKED).isEmpty());
    Assert.assertEquals(1, identifiersDao.findByIdentifierInAndStatus(allPossibleIdentifiers, DataResource.State.GONE).size());
   Assert.assertTrue(true);
  }

  @Test
  public void testDoubleEntry() throws Exception {
    AllIdentifiers identifier = createIdentifier("6", "anyResource", DataResource.State.FIXED);
    identifiersDao.save(identifier);
    String[] array = new String[]{"6"};
    List<String> allPossibleIdentifiers = new ArrayList<>();
    Collections.addAll(allPossibleIdentifiers, array);
     
    List<AllIdentifiers> findByIdentifierIn = identifiersDao.findByIdentifierIn(allPossibleIdentifiers);
    Assert.assertEquals(1, findByIdentifierIn.size());
    Assert.assertEquals(identifier, findByIdentifierIn.get(0));
  }
  @Test
  public void testFindByIdentifier() throws Exception {
    Optional<AllIdentifiers> result = identifiersDao.findById("first");
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("first", result.get().getIdentifier());
    Assert.assertEquals("resource", result.get().getResourceId());
    Assert.assertEquals(DataResource.State.GONE, result.get().getStatus());
    result = identifiersDao.findByIdentifierAndStatus("second", DataResource.State.GONE);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(two.getIdentifier(), result.get().getIdentifier());
    Assert.assertEquals(two.getResourceId(), result.get().getResourceId());
    Assert.assertEquals(two.getStatus(), result.get().getStatus());
    result = identifiersDao.findByIdentifierAndStatus("5", DataResource.State.FIXED);
    Assert.assertFalse(result.isPresent());
    result = identifiersDao.findByIdentifier("5");
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(five, result.get());
  }
}
