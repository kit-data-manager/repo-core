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
import edu.kit.datamanager.exceptions.CustomInternalServerError;
import edu.kit.datamanager.service.impl.FileArchiveContentCollectionProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.MediaType;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

/**
 *
 * @author jejkal
 */
public class FileArchiveContentCollectionProviderTest{

  @Test
  public void testCanProvide(){
    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    Assert.assertTrue(provider.canProvide("file"));
    Assert.assertFalse(provider.canProvide("http"));
    Assert.assertFalse(provider.canProvide(null));
  }

  @Test
  public void testGetSupportedMediaTypes(){
    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    Assert.assertArrayEquals(provider.getSupportedMediaTypes(), new MediaType[]{FileArchiveContentCollectionProvider.ZIP_MEDIA_TYPE});
  }

  @Test
  public void testSupportsMediaType(){
    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    Assert.assertTrue(provider.supportsMediaType(MediaType.parseMediaType("application/zip")));
    Assert.assertTrue(provider.supportsMediaType(FileArchiveContentCollectionProvider.ZIP_MEDIA_TYPE));
    Assert.assertFalse(provider.supportsMediaType(MediaType.parseMediaType("application/json")));
    Assert.assertFalse(provider.supportsMediaType(null));
  }

  @Test(expected = UnsupportedMediaTypeStatusException.class)
  public void testProvideInvalidMediaType(){
    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    provider.provide(null, MediaType.APPLICATION_JSON, null);
  }

  @Test(expected = CustomInternalServerError.class)
  @Ignore
  public void testProvideNotExistingFile() throws Exception{
    //@TODO re-activate using mocking of ServletResponse

    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    List<ContentElement> collection = new ArrayList<>();
    collection.add(ContentElement.createContentElement("123", "firstFile.txt", "1", "kitdm_simple"));
    collection.add(ContentElement.createContentElement("123", "secondFile.txt", "1", "kitdm_simple", "http://localhost:8090/api/v1/dataresources/123", 1l));
    collection.add(ContentElement.createContentElement("123", "thirdFile.txt", "1", "kitdm_simple", "sha256:123123123123", "http://localhost:8090/api/v1/dataresources/123", 1l));

    provider.provide(collection, FileArchiveContentCollectionProvider.ZIP_MEDIA_TYPE, null);
  }

  @Test
  @Ignore
  public void testProvide() throws Exception{
    //@TODO re-activate using mocking of ServletResponse

    FileArchiveContentCollectionProvider provider = new FileArchiveContentCollectionProvider();
    HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream(){
      @Override
      public boolean isReady(){
        return true;
      }

      @Override
      public void setWriteListener(WriteListener listener){

      }

      @Override
      public void write(int b) throws IOException{
        bout.write(b);
      }
    });

    //create paths
    Path firstFile = Paths.get(System.getProperty("java.io.tmpdir"), "firstFile.txt");
    Path secondFile = Paths.get(System.getProperty("java.io.tmpdir"), "secondFile.txt");
    Path thirdFile = Paths.get(System.getProperty("java.io.tmpdir"), "subFolder", "thirdFile.txt");
    if(!Files.exists(Paths.get(System.getProperty("java.io.tmpdir"), "subFolder"))){
      Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir"), "subFolder"));
    }
    //build filename-path map for checks later
    Map<String, Path> pathMap = new HashMap<>();
    pathMap.put(firstFile.getName(firstFile.getNameCount() - 1).toString(), firstFile);
    pathMap.put(secondFile.getName(secondFile.getNameCount() - 1).toString(), secondFile);
    pathMap.put(thirdFile.getName(thirdFile.getNameCount() - 1).toString(), thirdFile);
    List<ContentElement> collection = new ArrayList<>();

    try{
      //write content to files
      Files.write(firstFile, "This is ".getBytes());
      Files.write(secondFile, "a test! ".getBytes());
      Files.write(thirdFile, "And I'm from a subfolder.".getBytes());

      //create zip-collection map
      collection.add(ContentElement.createContentElement("123", "firstFile.txt", "1", "kitdm_simple"));
      collection.add(ContentElement.createContentElement("123", "secondFile.txt", "1", "kitdm_simple"));
      collection.add(ContentElement.createContentElement("123", "thirdFile.txt", "1", "kitdm_simple"));

      provider.provide(collection, FileArchiveContentCollectionProvider.ZIP_MEDIA_TYPE, response);

      //read zipped output from ByteArrayOutputStream
      ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
      ZipInputStream zin = new ZipInputStream(in);
      ZipEntry e = null;
      ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
      while((e = zin.getNextEntry()) != null){
        //obtain path element using zip entry name
        Path f = pathMap.get(e.getName());
        Assert.assertNotNull(f);
        int len;
        byte[] buffer = new byte[2048];
        //read zipped content
        while((len = zin.read(buffer)) > 0){
          bout2.write(buffer, 0, len);
        }
        //compare size from original file and zipped content (size is available not until the entry was read)
        Assert.assertEquals(Files.size(f), e.getSize());
      }
      //compare all read content with the expected summed up content of all files (this will fail if files are unzipped in a different order...can this happen?)
      Assert.assertEquals("This is a test! And I'm from a subfolder.", bout2.toString());
    } finally{
      Files.delete(firstFile);
      Files.delete(secondFile);
      Files.delete(thirdFile);
    }

  }

}
