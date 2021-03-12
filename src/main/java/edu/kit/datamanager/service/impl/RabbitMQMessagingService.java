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
package edu.kit.datamanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.configuration.RabbitMQConfiguration;
import edu.kit.datamanager.entities.messaging.IAMQPSubmittable;
import org.springframework.beans.factory.annotation.Autowired;
import edu.kit.datamanager.service.IMessagingService;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;

/**
 *
 * @author jejkal
 */
@Service
public class RabbitMQMessagingService implements IMessagingService {

    @Autowired
    private RabbitMQConfiguration configuration;
    @Autowired
    private Logger logger;

    @Override
    public void send(IAMQPSubmittable msg) {
        logger.trace("Processing new AMQPSubmittable via RabbitMQMessagingService.");
        if (configuration.isMessagingEnabled()) {
            logger.trace("Messaging enabled, serializing and submitting message.");
            try {
                String msgString = msg.toJson();
                String msgRoute = msg.getRoutingKey();
                String exchangeName = configuration.rabbitMQExchange().getName();
                logger.trace("Sending message {} via exchange {} and route {}.", msgString, exchangeName, msgRoute);
                configuration.rabbitMQTemplate().convertAndSend(configuration.rabbitMQExchange().getName(), msgRoute, msgString);
                logger.trace("Message sent.");
            } catch (JsonProcessingException ex) {
                logger.error("Failed to send message " + msg + ". Unable to serialize message to JSON.", ex);
            }
        } else {
            logger.trace("Messaging is disabled. All messages are discarded.");
        }
    }

    @Override
    public Health health() {
        logger.trace("Obtaining health information.");
        return Health.up().withDetail("RabbitMQMessaging", configuration.rabbitMQExchange()).build();
    }

}
