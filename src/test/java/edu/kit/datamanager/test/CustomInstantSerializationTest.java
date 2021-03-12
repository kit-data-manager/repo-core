/*
 * Copyright 2019 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import edu.kit.datamanager.util.json.CustomInstantDeserializer;
import edu.kit.datamanager.util.json.CustomInstantSerializer;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author jejkal
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomInstantSerializationTest{

  @Mock
  private JsonGenerator gen;

  @Mock
  private JsonParser pars;

  @Test
  public void testSerializeInstant() throws Exception{
    Instant instant = Instant.ofEpochMilli(0);
    new CustomInstantSerializer().serialize(instant, gen, null);
    String expectedOutput = "1970-01-01T00:00:00Z";
    verify(gen, times(1)).writeString(expectedOutput);
  }

  @Test
  public void testDeserializeInstant() throws Exception{
    Instant start = Instant.ofEpochMilli(0);
    when(pars.getText()).thenReturn("1970-01-01T00:00:00Z");
    Instant inst = new CustomInstantDeserializer().deserialize(pars, null);
    Assert.assertEquals(inst, start);
  }

  @Test
  public void testNullSerialization() throws Exception{
    new CustomInstantSerializer().serialize(null, gen, null);
    verify(gen, times(1)).writeString("");
  }

  @Test
  public void testNullDeserialization() throws Exception{
    when(pars.getText()).thenReturn(null);
    Instant inst = new CustomInstantDeserializer().deserialize(pars, null);
    Assert.assertNull(inst);
  }

  @Test
  public void testEmptyDeserialization() throws Exception{
    when(pars.getText()).thenReturn("");
    Instant inst = new CustomInstantDeserializer().deserialize(pars, null);
    Assert.assertNull(inst);
  }

  @Test(expected = DateTimeParseException.class)
  public void testInvalidDeserializationInput() throws Exception{
    when(pars.getText()).thenReturn("no-instant");
    Instant inst = new CustomInstantDeserializer().deserialize(pars, null);
  }

}
