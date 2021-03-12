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
package edu.kit.datamanager.test;

import edu.kit.datamanager.entities.RepoUserRole;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jejkal
 */
public class RepoUserRoleTest{

  @Test
  public void testFromValue(){
    Assert.assertEquals(RepoUserRole.fromValue("ROLE_CURATOR"), RepoUserRole.CURATOR);
    Assert.assertEquals(RepoUserRole.fromValue("ROLE_ADMINISTRATOR"), RepoUserRole.ADMINISTRATOR);
    Assert.assertEquals(RepoUserRole.fromValue("ROLE_USER"), RepoUserRole.USER);
    Assert.assertEquals(RepoUserRole.fromValue("ROLE_GUEST"), RepoUserRole.GUEST);
    Assert.assertEquals(RepoUserRole.fromValue("ROLE_INACTIVE"), RepoUserRole.INACTIVE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromIllegalValue(){
    RepoUserRole.fromValue("ROLE_INVALID_TEST");
  }

  @Test
  public void testGetValueMatchesToString(){
    Assert.assertEquals(RepoUserRole.CURATOR.getValue(), RepoUserRole.CURATOR.toString());
  }
}
