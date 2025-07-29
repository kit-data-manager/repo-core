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
package edu.kit.datamanager.repo.dao;

import edu.kit.datamanager.entities.PERMISSION;
import edu.kit.datamanager.repo.domain.acl.AclEntry;
import edu.kit.datamanager.repo.util.MonitoringUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

/**
 * Test for {@link IIpMonitoringDao}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //RANDOM_PORT)
@EntityScan("edu.kit.datamanager")
@EnableJpaRepositories("edu.kit.datamanager")
@ComponentScan({"edu.kit.datamanager"})
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {"server.port=41421"})
@TestPropertySource(properties = {"repo.monitoring.enabled=true"})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:db_acl_entry_dao;DB_CLOSE_DELAY=-1;MODE=LEGACY;NON_KEYWORDS=VALUE"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IAclEntryDaoTest {

  private static final String[] sids = {"sid1", "sid2", "sid3", "sid4"};
  @Autowired
  private IAclEntryDao aclEntryDao;

  public IAclEntryDaoTest() {
  }

  @BeforeClass
  public static void setUpClass() {

  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
    aclEntryDao.deleteAll();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testNoOfSids() {
    System.out.println("testNoOfSids");
    for (int noOfEntries = 1; noOfEntries < sids.length * 2; noOfEntries++) {
      prepareDataBase(noOfEntries);
      Assert.assertEquals(Math.min(noOfEntries, sids.length), aclEntryDao.countRegisteredSids());
      Assert.assertEquals(noOfEntries, aclEntryDao.count());
    }
  }

  @Test
  public void testMonitoringUtil4Sids() {
    System.out.println("testMonitoringUtil4Sids");
    for (int noOfEntries = 1; noOfEntries < sids.length * 2; noOfEntries++) {
      prepareDataBase(noOfEntries);
      Assert.assertEquals(Math.min(noOfEntries, sids.length), MonitoringUtil.getNoOfRegisteredUsers());
      Assert.assertEquals(noOfEntries, aclEntryDao.count());
    }
  }

  private void prepareDataBase(int noOfEntries) {
    AclEntry aclEntry;
    aclEntryDao.deleteAll();
    for (int i = 0; i < noOfEntries; i++) {
      aclEntry = new AclEntry();
      aclEntry.setSid(sids[i % sids.length]);
      aclEntry.setPermission(PERMISSION.NONE);
      aclEntryDao.save(aclEntry);
    }
    Assert.assertEquals(noOfEntries, aclEntryDao.count());
  }
}
