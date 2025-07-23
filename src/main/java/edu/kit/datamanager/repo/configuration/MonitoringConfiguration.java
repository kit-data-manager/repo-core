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
package edu.kit.datamanager.repo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for Monitoring.
 */
@ConfigurationProperties(prefix = "repo.monitoring")
@Component
@Data
@Validated
@EnableScheduling
public class MonitoringConfiguration {
  /**
   * Whether the monitoring is enabled or not. If set to false, no
   * monitoring metrics will be provided.
   */
  private boolean enabled = false;

  /**
   * The name of the service to be monitored.
   */
  private String serviceName = "repo_service";

  /**
   * The number of days to keep the hash of the ips.
   */
  private int noOfDaysToKeep = 28; // 4 weeks
}
