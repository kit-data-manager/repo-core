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
package edu.kit.datamanager.repo.scheduler;

import edu.kit.datamanager.repo.service.impl.MonitoringService;
import edu.kit.datamanager.repo.util.MonitoringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Task for daily clean-up monitoring.
 * This class is intended to be used as a scheduled task that performs clean-up operations related to monitoring data.
 */
@Component
public class DailyCleanUpMonitoringTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(DailyCleanUpMonitoringTask.class);

  @Autowired
  private MonitoringService monitoringService;

  @Scheduled(cron = "0 10 0 * * ?") // Runs every day at midnight
  public void cleanUp() {
    LOGGER.info("Cleaning up DailyCleanUpMonitoringTask");
    monitoringService.cleanUpMetrics();
  }
}
