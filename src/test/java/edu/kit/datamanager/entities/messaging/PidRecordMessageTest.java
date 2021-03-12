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
package edu.kit.datamanager.entities.messaging;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Andreas Pfeil
 */
public class PidRecordMessageTest {

  @Test
  public void testFactoryPidRecordMessage() {
    for (PidRecordMessage.ACTION action : PidRecordMessage.ACTION.values()) {
      performFactoryPidRecordMessageTest(action);
    }
  }

  /**
   * Helper to create data resource messages for each action.
   */
  private void performFactoryPidRecordMessageTest(PidRecordMessage.ACTION action) {

    PidRecordMessage msg;
    String pid = "/pid/test/helloworld";
    String url = "https://record.url/";
    String principal = "principal";
    String sender = "sender";
    String entityName = "pidrecord";

    switch (action) {
      case ADD:
        msg = PidRecordMessage.creation(pid, url, principal, sender);
        break;
      case UPDATE:
        msg = PidRecordMessage.update(pid, url, principal, sender);
        break;
      default:
        msg = PidRecordMessage.creation(pid, url, principal, sender);
        break;
    }
    Assert.assertEquals(entityName, msg.getEntityName());
    Assert.assertEquals(principal, msg.getPrincipal());
    Assert.assertEquals(sender, msg.getSender());
    Assert.assertNotNull(msg.getTimestamp());

    Assert.assertEquals(pid, msg.getEntityId());
    Assert.assertEquals(action.getValue(), msg.getAction());
    Assert.assertEquals(entityName + "." + action.getValue(), msg.getRoutingKey());
  }
}
