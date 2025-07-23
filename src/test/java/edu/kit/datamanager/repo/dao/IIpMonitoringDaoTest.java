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

import edu.kit.datamanager.repo.domain.IpMonitoring;
import edu.kit.datamanager.repo.service.impl.MonitoringService;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
@TestPropertySource(properties = {"server.port=41420"})
@TestPropertySource(properties = {"repo.monitoring.enabled=true"})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:db_ip_monitoring_dao;DB_CLOSE_DELAY=-1;MODE=LEGACY;NON_KEYWORDS=VALUE"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class IIpMonitoringDaoTest {

  private static final String[] ips = {"ip1", "ip2", "ip3", "ip4"};
  @Autowired
  private MonitoringService monitoringService;
  @Autowired
  private IIpMonitoringDao monitoringDao;

  public IIpMonitoringDaoTest() {
  }

  @BeforeClass
  public static void setUpClass() {

  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
    monitoringDao.deleteAll();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testAccumulation() {
    System.out.println("testAccumulation");
    for (int noOfEntries = 1; noOfEntries < 20; noOfEntries++) {
      prepareDataBase(noOfEntries);
      Assert.assertEquals(Math.min(noOfEntries, ips.length), monitoringDao.count());
    }
  }

  @Test
  public void testCleanUp() {
    // Skip test on Windows as it uses H2 in-memory database
    // which does not work properly on Windows.
    String os = System.getProperty("os.name").toLowerCase();
    Assume.assumeFalse(os.contains("win"));

    System.out.println("testCleanUp");
    prepareDataBase(20);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length, monitoringDao.count());
    prepareDataBase(28);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length, monitoringDao.count());
    prepareDataBase(29);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length - 1, monitoringDao.count());
    prepareDataBase(30);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length - 2, monitoringDao.count());
    prepareDataBase(31);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length - 3, monitoringDao.count());
    prepareDataBase(32);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(ips.length - 4, monitoringDao.count());
    prepareDataBase(33);
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(0, monitoringDao.count());
  }

  @Test
  public void testMonitoringUtil() {
    System.out.println("testMonitoringUtil");
    monitoringDao.deleteAll();
    // Test for null IP
    MonitoringUtil.registerIp(null);
    Assert.assertEquals(0, monitoringDao.count());
    // Test for empty IP
    MonitoringUtil.registerIp("");
    Assert.assertEquals(0, monitoringDao.count());
    // Test for empty IP
    MonitoringUtil.registerIp("  ");
    Assert.assertEquals(0, monitoringDao.count());
    // Test for valid IP
    MonitoringUtil.registerIp("ip1");
    Assert.assertEquals(1, monitoringDao.count());
    // Test for getting the number of unique users
    Assert.assertEquals(1, MonitoringUtil.getNoOfUniqueUsers());
    // Test for getting the number of unique users after cleanup
    monitoringService.cleanUpMetrics();
    Assert.assertEquals(1, MonitoringUtil.getNoOfUniqueUsers());
  }

  private void prepareDataBase(int noOfEntries) {
    IpMonitoring ipMonitoring = new IpMonitoring();
    monitoringDao.deleteAll();
    for (int i = 0; i < noOfEntries; i++) {
      ipMonitoring.setIpHash(ips[i % ips.length]);
      ipMonitoring.setLastVisit(nowMinusDays(i));
      monitoringDao.save(ipMonitoring);
    }
  }

  private Instant nowMinusDays(int days) {
    return Instant.now().minus(days, ChronoUnit.DAYS);
  }
}
