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
package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.repo.configuration.MonitoringConfiguration;
import edu.kit.datamanager.repo.dao.IIpMonitoringDao;
import edu.kit.datamanager.repo.dao.IAclEntryDao;
import edu.kit.datamanager.repo.domain.IpMonitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for providing monitoring functionality.
 */
public class MonitoringUtil {
  /**
   * Logger for messages.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MonitoringUtil.class);
  /**
   * Instance holding configuration for monitoring.
   */
  private static MonitoringConfiguration monitoringConfiguration;
  /**
   * Instance holding the repository for the IP monitoring.
   */
  private static IIpMonitoringDao ipMonitoringDao;
  /**
   * Instance holding the repository for the ACL entries.
   */
  private static IAclEntryDao aclEntryDao;
  /**
   * Sets the monitoring configuration.
   *
   * @param monitoringConfiguration the monitoringConfiguration to set
   */
  public static void setMonitoringConfiguration(MonitoringConfiguration monitoringConfiguration) {
    MonitoringUtil.monitoringConfiguration = monitoringConfiguration;
  }

  /**
   * Sets the repository for the IP monitoring.
   *
   * @param ipMonitoringDao the ipMonitoringDao to set
   */
  public static void setIpMonitoringDao(IIpMonitoringDao ipMonitoringDao) {
    MonitoringUtil.ipMonitoringDao = ipMonitoringDao;
  }

  /**
   * Sets the repository for the ACL entries.
   *
   * @param aclEntryDao the aclEntryDao to set
   */
  public static void setAclEntryDao(IAclEntryDao aclEntryDao) {
    MonitoringUtil.aclEntryDao = aclEntryDao;
  }

  /**
   * Get the name of the service.
   *
   * @return the name of the service
   */
  public static String getServiceName() {
    String serviceName = "DefaultServiceName";
    if (monitoringConfiguration != null) {
      serviceName = monitoringConfiguration.getServiceName();
    } else {
      LOG.warn("Monitoring configuration is not set. Returning default service name: '{}'.", serviceName);
    }
    return serviceName;
  }

  /**
   * Returns the number of unique users of the last number of days configured.
   */
  public static long getNoOfUniqueUsers() {
    long noOfUniqueUsers = 0;
    if (isMonitoringEnabled() && ipMonitoringDao != null) {
      noOfUniqueUsers = ipMonitoringDao.count();
    }
    return noOfUniqueUsers;
  }

  /**
   * Returns the number of registered users in total.
   */
  public static long getNoOfRegisteredUsers() {
    long noOfRegisteredUsers = 0;
    if (isMonitoringEnabled() && aclEntryDao != null) {
      noOfRegisteredUsers = aclEntryDao.countRegisteredSids();
    }
    return noOfRegisteredUsers;
  }

  /**
   * Register the IP hash for the given IP address.
   *
   * @param ip The IP address to hash.
   */
  public static void registerIp(String ip) {
    if (isMonitoringEnabled()) {
      // Check if IP address is null or empty
      if (ip == null || ip.trim().isEmpty()) {
        LOG.warn("IP address is null or empty. Cannot register.");
        return;
      }

      String ipHash = ip;
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(ip.getBytes(StandardCharsets.UTF_8));
        ipHash = new String(messageDigest.digest(), StandardCharsets.UTF_8);
      } catch (NoSuchAlgorithmException nsae) {
        LOG.error("Error hashing IP address: ", nsae);
      }
      // Store the IP hash or, if hashing fails, the ip in the database
      if (ipMonitoringDao != null) {
        IpMonitoring ipMonitoring = new IpMonitoring();
        ipMonitoring.setIpHash(ipHash);
        ipMonitoring.setLastVisit(Instant.now().truncatedTo(ChronoUnit.DAYS));
        ipMonitoringDao.save(ipMonitoring);
      }
    }
  }

  /**
   * Cleans up the metrics by deleting records older than the specified number of days.
   */
  public static void cleanUpMetrics() {
    if (isMonitoringEnabled() && ipMonitoringDao != null) {
      LOG.info("Cleaning up metrics older than {} days", monitoringConfiguration.getNoOfDaysToKeep());
      Instant latestDate = Instant.now().minus(monitoringConfiguration.getNoOfDaysToKeep(), ChronoUnit.DAYS);
      ipMonitoringDao.deleteAllEntriesOlderThan(latestDate);
    }
  }

  /**
   * Checks if monitoring is enabled.
   *
   * @return true if monitoring is enabled, false otherwise
   */
  public static boolean isMonitoringEnabled() {
    return monitoringConfiguration != null && monitoringConfiguration.isEnabled();
  }
}

