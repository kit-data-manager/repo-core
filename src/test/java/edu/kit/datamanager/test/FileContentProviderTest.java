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

import edu.kit.datamanager.entities.ContentElement;
import edu.kit.datamanager.exceptions.ResourceNotFoundException;
import edu.kit.datamanager.service.impl.FileContentProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 *
 * @author jejkal
 */
public class FileContentProviderTest{

  @Test
  public void testCanProvide(){
    FileContentProvider prov = new FileContentProvider();
    Assert.assertFalse(prov.canProvide("http"));
    Assert.assertFalse(prov.canProvide("https"));
    Assert.assertTrue(prov.canProvide("file"));
  }

  @Test(expected = ResourceNotFoundException.class)
  @Ignore
  public void testProvideInvalidFile(){
    //@TODO re-activate using mocking of ServletResponse
    FileContentProvider prov = new FileContentProvider();
    prov.provide(ContentElement.createContentElement("123", "notExist.txt", "1", "kitdm_simple"), MediaType.APPLICATION_JSON, "doesNotExist.123", null);
  }

  @Test
  @Ignore
  public void testProvideSuccessful() throws Exception{

    //@TODO re-activate using mocking of ServletResponse
//    String tmp = System.getProperty("java.io.tmpdir");
//    Path p = Paths.get(tmp, "testFile.txt");
//    try{
//      Files.createFile(p);
//      Files.write(p, "This is a test".getBytes());
//      FileContentProvider prov = new FileContentProvider();
//      prov.provide(p.toUri(), MediaType.APPLICATION_JSON, "doesNotExist.123");
//      Assert.assertEquals(respo.getStatusCode(), HttpStatus.OK);
//      Assert.assertEquals(respo.getBody().getClass(), FileSystemResource.class);
//      Assert.assertEquals(((FileSystemResource) respo.getBody()).getURI(), p.toUri());
//    } finally{
//      Files.delete(p);
//    }
  }

}
