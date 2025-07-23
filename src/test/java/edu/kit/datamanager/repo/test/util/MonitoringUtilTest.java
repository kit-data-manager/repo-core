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
package edu.kit.datamanager.repo.test.util;

import edu.kit.datamanager.repo.configuration.MonitoringConfiguration;
import edu.kit.datamanager.repo.util.MonitoringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MonitoringUtilTest {
  @Test
  public void testGetServiceName() {
    MonitoringUtil.setMonitoringConfiguration(null);
    String serviceName = MonitoringUtil.getServiceName();
    Assert.assertNotNull(serviceName);
    Assert.assertFalse(serviceName.isEmpty());
    Assert.assertEquals("DefaultServiceName", serviceName);
  }

  @Test
  public void testRegisterIp() {
    String ip = null;
    MonitoringUtil.registerIp(ip);
    ip = "   ";
    MonitoringUtil.registerIp(ip);
    MonitoringUtil.setMonitoringConfiguration(null);
    MonitoringUtil.registerIp(ip);
    try (MockedStatic<MessageDigest> messageDigestMockedStatic = Mockito.mockStatic(MessageDigest.class)) {
      messageDigestMockedStatic.when(() -> MessageDigest.getInstance("SHA-256")).thenThrow(new NoSuchAlgorithmException("Test exception"));
      // Test on machine without SHA-256 support
      try {
        MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration();
        monitoringConfiguration.setEnabled(true);
        MonitoringUtil.setMonitoringConfiguration(monitoringConfiguration);
        MonitoringUtil.setIpMonitoringDao(null);
        MonitoringUtil.registerIp("anyIp");
      } catch (Exception e) {
        Assert.fail("Unexpected exception: " + e.getMessage());
      }
    }
  }

  @Test
  public void testMonitoringEnabled() {
    MonitoringUtil.setMonitoringConfiguration(null);
    boolean isEnabled = MonitoringUtil.isMonitoringEnabled();
    Assert.assertFalse(isEnabled);
    MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration();
    monitoringConfiguration.setEnabled(false);
    MonitoringUtil.setMonitoringConfiguration(monitoringConfiguration);
    isEnabled = MonitoringUtil.isMonitoringEnabled();
    Assert.assertFalse(isEnabled);
  }

  @Test
  public void testCleanUpMetrics() {
    try {
      MonitoringUtil.setMonitoringConfiguration(null);
      MonitoringUtil.cleanUpMetrics();
      MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration();
      monitoringConfiguration.setEnabled(false);
      MonitoringUtil.setMonitoringConfiguration(monitoringConfiguration);
      MonitoringUtil.cleanUpMetrics();
      MonitoringUtil.setIpMonitoringDao(null);
      MonitoringUtil.cleanUpMetrics();
      monitoringConfiguration.setEnabled(true);
      MonitoringUtil.cleanUpMetrics();
      Assert.assertTrue(true);
    } catch (Exception e) {
      Assert.fail("Unexpected exception: " + e.getMessage());
    }
  }
  @Test
  public void testGetNoOfUniqueUsers() {
    MonitoringUtil.setMonitoringConfiguration(null);
    long noOfUniqueUsers = MonitoringUtil.getNoOfUniqueUsers();
    Assert.assertEquals(0, noOfUniqueUsers);
    MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration();
    monitoringConfiguration.setEnabled(true);
    MonitoringUtil.setIpMonitoringDao(null);
    noOfUniqueUsers = MonitoringUtil.getNoOfUniqueUsers();
    Assert.assertEquals(0, noOfUniqueUsers);
  }
}
