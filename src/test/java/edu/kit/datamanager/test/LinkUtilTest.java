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

import edu.kit.datamanager.util.LinkUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.el.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jejkal
 */
public class LinkUtilTest{

  @Test
  public void testLinkCreation(){
    Assert.assertEquals("<http://localhost:8080/myService?page=1>; rel=\"next\"", LinkUtil.createLinkHeader("http://localhost:8080/myService?page=1", LinkUtil.REL_NEXT));
    Assert.assertEquals("<http://localhost:8080/myService?page=1>; rel=\"first\"", LinkUtil.createLinkHeader("http://localhost:8080/myService?page=1", LinkUtil.REL_FIRST));
    Assert.assertEquals("<http://localhost:8080/myService?page=1>; rel=\"last\"", LinkUtil.createLinkHeader("http://localhost:8080/myService?page=1", LinkUtil.REL_LAST));
    Assert.assertEquals("<http://localhost:8080/myService?page=1>; rel=\"prev\"", LinkUtil.createLinkHeader("http://localhost:8080/myService?page=1", LinkUtil.REL_PREV));
    Assert.assertEquals("<http://localhost:8080/myService?page=1>; rel=\"collection\"", LinkUtil.createLinkHeader("http://localhost:8080/myService?page=1", LinkUtil.REL_COLLECTION));
  }

}
