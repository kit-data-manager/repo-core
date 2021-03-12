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

import edu.kit.datamanager.util.ControllerUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author jejkal
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({InetAddress.class, ControllerUtils.class})
public class GetLocalHostnameTest{

  @Test
  public void testGetLocalHostname() throws Exception{
    InetAddress a = InetAddress.getByName("episteme2.scc.kit.edu");

    PowerMockito.mockStatic(InetAddress.class);
    PowerMockito.when(InetAddress.getLocalHost()).thenReturn(a);
    Assert.assertEquals("episteme2.scc.kit.edu", ControllerUtils.getLocalHostname());
  }

  @Test
  public void getGetLocalHostnameFail() throws Exception{
    PowerMockito.mockStatic(InetAddress.class);
    PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException());
    Assert.assertEquals("localhost", ControllerUtils.getLocalHostname());
  }
}
