/*
 * Copyright 2021 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.repo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.entities.messaging.IAMQPSubmittable;
import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Very simple AMQP message entity holding the target exchange and routingKey as
 * well as the already JSON-serialized message.
 *
 * @author Jejkal
 */
@Entity
@Data
public class AMQPMessage implements IAMQPSubmittable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String exchange;
    private String routingKey;
    private String message;

    public AMQPMessage() {
    }

    public AMQPMessage(String exchange, String routingKey, String message) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.message = message;
    }

    @Override
    public void validate() {
        //always successful as the input comes from a valid IAMQPSubmittable
    }

    @Override
    public String toJson() throws JsonProcessingException {
        //no fancy stuff here, only return the message which was already serialized by the original IAMQPSubmittable
        return message;
    }

}
