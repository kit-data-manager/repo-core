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
package edu.kit.datamanager.repo.service.impl;

import edu.kit.datamanager.repo.configuration.MonitoringConfiguration;
import edu.kit.datamanager.repo.dao.IIpMonitoringDao;
import edu.kit.datamanager.repo.util.MonitoringUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class for initializing utility class for monitoring functionality.
 */
@Component
public class MonitoringService implements HandlerInterceptor, WebMvcConfigurer {
  /**
   * Logger for messages.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);
  /**
   * Counter for the number of requests served.
   */
  private final Counter counter;
  /**
   * MeterRegistry for registering metrics.
   */
  MeterRegistry meterRegistry;
  /**
   * Configuration for monitoring.
   */
  private MonitoringConfiguration monitoringConfiguration;
  /**
   * DAO for IP monitoring.
   */
  private IIpMonitoringDao ipMonitoringDao;
  /**
   * Constructor for MonitoringService.
   * Initializes the MonitoringUtil with the provided configuration and DAO.
   */
  @Autowired
  public MonitoringService(MeterRegistry meterRegistry,
                           MonitoringConfiguration monitoringConfiguration,
                           IIpMonitoringDao ipMonitoringDao) {
    LOGGER.info("MonitoringUtil initialized with configuration: {}", monitoringConfiguration);
    MonitoringUtil.setMonitoringConfiguration(monitoringConfiguration);
    MonitoringUtil.setIpMonitoringDao(ipMonitoringDao);

    LOGGER.trace("Initializing MonitoringService with service name: {}", MonitoringUtil.getServiceName());
    // Register a gauge for the number of unique users
    String prefixMetrics = MonitoringUtil.getServiceName();
    Gauge.builder( prefixMetrics + "_unique_users", MonitoringUtil::getNoOfUniqueUsers).register(meterRegistry);
    counter = Counter.builder(prefixMetrics + "_requests_served").register(meterRegistry);
  }

  @Override
  public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) throws Exception {
    LOGGER.trace("PreHandleInterceptor: preHandle called for request: {}", request.getRequestURI());
    if (MonitoringUtil.isMonitoringEnabled()) {
      String forwardedFor = request.getHeader("X-Forwarded-For");
      LOGGER.debug("X-Forwarded-For: {}", forwardedFor);
      String clientIp = null;

      if (forwardedFor != null) {
        String[] ipList = forwardedFor.split(", ");
        if (ipList.length > 0) clientIp = ipList[0];
        LOGGER.debug("Client IP from X-Forwarded-For: {}", clientIp);
      }

      String remoteIp = request.getRemoteAddr();
      LOGGER.debug("Client IP from getRemoteAddr: {}", remoteIp);
      String ip = clientIp == null ? remoteIp : clientIp;
      LOGGER.debug("Using {} for monitoring", ip);

      MonitoringUtil.registerIp(ip);

      counter.increment();
    }

    return true;
  }

  /**
   * Clean up the metrics for IP addresses that are older than the configured number of days.
   */
  @Transactional
  public void cleanUpMetrics() {
    MonitoringUtil.cleanUpMetrics();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Only register the interceptor if monitoring is enabled
    if (MonitoringUtil.isMonitoringEnabled()) {
      registry.addInterceptor(this);
    }
  }
}
