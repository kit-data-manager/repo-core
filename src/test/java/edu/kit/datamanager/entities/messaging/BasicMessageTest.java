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

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.exceptions.MessageValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jejkal
 */
public class BasicMessageTest{

  @Test
  public void test() throws JsonProcessingException, IOException{
    BasicMessage msg = new BasicMessage(){
      @Override
      public String getEntityName(){
        return "Test";
      }
    };

    msg.setPrincipal("tester");
    msg.setSender("localhost");
    msg.setCurrentTimestamp();

    msg.setEntityId("1");
    msg.setAction("create");

    Assert.assertEquals("Test", msg.getEntityName());
    Assert.assertEquals("tester", msg.getPrincipal());
    Assert.assertEquals("localhost", msg.getSender());
    Assert.assertNotNull(msg.getTimestamp());

    Assert.assertEquals("1", msg.getEntityId());
    Assert.assertEquals("create", msg.getAction());

    //test routing key creation and lowercase entity name
    Assert.assertEquals("test.create", msg.getRoutingKey());

    //test lowercase action
    msg.setAction("Create");
    Assert.assertEquals("test.create", msg.getRoutingKey());

    //test subcategory
    msg.setSubCategory("data");
    Assert.assertEquals("test.create.data", msg.getRoutingKey());

    //test lowercase subcategory
    msg.setSubCategory("Data");
    Assert.assertEquals("test.create.data", msg.getRoutingKey());

    Map<String, String> properties = new HashMap<>();
    properties.put("key", "value");
    properties.put("key2", "anotherValue");
    msg.setMetadata(properties);

    Assert.assertNotNull(msg.getMetadata());
    Assert.assertEquals(2, msg.getMetadata().size());

    String toJson = msg.toJson();
    System.out.println(toJson);
    BasicMessage msg2 = BasicMessage.fromJson(toJson);

    //some fields are not ignored and must be equal
    Assert.assertEquals(msg.getEntityId(), msg2.getEntityId());
    Assert.assertEquals(msg.getAction(), msg2.getAction());
    Assert.assertEquals(msg.getSubCategory(), msg2.getSubCategory());
    Assert.assertEquals(msg.getSender(), msg2.getSender());
    Assert.assertEquals(msg.getTimestamp(), msg2.getTimestamp());
    Assert.assertEquals(msg.getMetadata(), msg2.getMetadata());
    
  }

  @Test(expected = MessageValidationException.class)
  public void testInvalidEntityName(){
    BasicMessage msg = new BasicMessage(){
      @Override
      public String getEntityName(){
        return null;
      }
    };

    msg.validate();
  }

  @Test(expected = MessageValidationException.class)
  public void testInvalidAction(){
    BasicMessage msg = new BasicMessage(){
      @Override
      public String getEntityName(){
        return "test";
      }
    };

    msg.setAction(null);
    msg.validate();
  }

  @Test(expected = MessageValidationException.class)
  public void testEntityId(){
    BasicMessage msg = new BasicMessage(){
      @Override
      public String getEntityName(){
        return "test";
      }
    };

    msg.setAction("create");
    msg.setEntityId(null);
    msg.validate();
  }
}
