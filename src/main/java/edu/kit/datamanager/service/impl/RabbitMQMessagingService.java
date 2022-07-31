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
import edu.kit.datamanager.repo.dao.IAMQPMessageDao;
import edu.kit.datamanager.repo.dao.DummyAMQPMessageDao;
import edu.kit.datamanager.repo.domain.AMQPMessage;
import edu.kit.datamanager.repo.service.impl.DataResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import edu.kit.datamanager.service.IMessagingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private Optional<IAMQPMessageDao> messageDao;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQMessagingService.class);

    @Override
    public void send(IAMQPSubmittable msg) {
        logger.trace("Processing new AMQPSubmittable via RabbitMQMessagingService.");
        if (configuration.isMessagingEnabled()) {
            logger.trace("Messaging enabled, serializing and submitting message.");
            boolean messagePreservationRequired = true;
            String msgString = null;
            String msgRoute = null;
            String exchangeName = null;
            try {
                msgString = msg.toJson();
                msgRoute = msg.getRoutingKey();
                exchangeName = configuration.rabbitMQExchange().getName();
                logger.trace("Sending message {} via exchange {} and route {}.", msgString, exchangeName, msgRoute);
                configuration.rabbitMQTemplate().convertAndSend(configuration.rabbitMQExchange().getName(), msgRoute, msgString);
                logger.trace("Message sent.");
                messagePreservationRequired = false;
                checkAndSendPreservedMessages();
            } catch (JsonProcessingException ex) {
                logger.error("Failed to send message " + msg + ". Unable to serialize message to JSON.", ex);
                messagePreservationRequired = false;
            } catch (AmqpConnectException amqpce) {
                logger.error("Failed to send message. Connection to message queue failed.", amqpce);
            } catch (AmqpException e) {
                logger.error("Failed to send message. Unexpected exception occured.", e);
            } finally {
                if (messagePreservationRequired) {
                    AMQPMessage messageToPersist = new AMQPMessage(exchangeName, msgRoute, msgString);
                    logger.trace("Persisting unsent AMQP message to database.");
                    messageToPersist = messageDao.orElse(new DummyAMQPMessageDao()).save(messageToPersist);
                    logger.trace("AMQP message successfully persisted with id {} for later submission.", messageToPersist.getId());
                }
            }
        } else {
            logger.trace("Messaging is disabled. All messages are discarded.");
        }
    }

    /**
     * Resubmission of messages stored in the database in case the message queue
     * is down.
     */
    private void checkAndSendPreservedMessages() {
        logger.trace("Checking for unsubmitted messages.");
        Page<AMQPMessage> messages = messageDao.orElse(new DummyAMQPMessageDao()).findAll(PageRequest.of(0, 100));

        int count = messages.getContent().size();
        logger.trace("Found {} unsubmitted messages in database.", count);
        messages.getContent().stream().map(msg -> {
            logger.trace("Removing message #{} from database.", msg.getId());
            return msg;
        }).map(msg -> {
            messageDao.orElse(new DummyAMQPMessageDao()).delete(msg);
            return msg;
        }).map(msg -> {
            logger.trace("Resending message.");
            return msg;
        }).forEachOrdered(msg -> {
            send(msg);
        });
        logger.trace("{} unsubmitted messages remaining.", (messages.getTotalElements() - count));
    }

    @Override
    public Health health() {
        logger.trace("Obtaining health information.");
        return Health.up().withDetail("RabbitMQMessaging", configuration.rabbitMQExchange()).build();
    }

}
