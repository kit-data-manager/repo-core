/*
 * Copyright 2022 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.entities.messaging.IAMQPSubmittable;
import edu.kit.datamanager.service.IMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author jejkal
 */
public class LogfileMessagingService implements IMessagingService {

    private Logger logger = LoggerFactory.getLogger(LogfileMessagingService.class);

    @Override
    public void send(IAMQPSubmittable msg) {
        try {
            logger.debug("Received message for route {} with content {}.", msg.getRoutingKey(), msg.toJson());
        } catch (JsonProcessingException ex) {
            logger.error("Failed to extract message information from message for route " + msg.getRoutingKey(), ex);
        }
    }

    @Override
    public Health health() {
        return Health.up().build();
    }

}
