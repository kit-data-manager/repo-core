/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package edu.kit.datamanager.repo.dao;

import edu.kit.datamanager.repo.domain.AMQPMessage;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for storing AMQP messages temporarily in case they could not
 * be submitted to the broker.
 *
 * @author Jejkal
 */
public interface IAMQPMessageDao extends JpaRepository<AMQPMessage, Long> {
}
