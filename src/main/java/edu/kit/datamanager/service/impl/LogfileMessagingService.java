/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.kit.datamanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.entities.messaging.IAMQPSubmittable;
import edu.kit.datamanager.service.IMessagingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author Torridity
 */
public class LogfileMessagingService implements IMessagingService {

    @Autowired
    private Logger logger;

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
