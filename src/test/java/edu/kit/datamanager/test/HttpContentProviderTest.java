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

import edu.kit.datamanager.service.impl.HttpContentProvider;
import java.io.IOException;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jejkal
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({HttpClients.class})
public class HttpContentProviderTest{

  private final CloseableHttpClient mock = PowerMockito.mock(CloseableHttpClient.class);
  private final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
  private final StatusLine line = mock(StatusLine.class);
  private final Header header = mock(Header.class);

  @Test
  @Ignore
  public void testHttpContentProvider() throws Exception{
    //@TODO re-activate after mocking ServletResponse

//    PowerMockito.mockStatic(HttpClients.class);
//    PowerMockito.when(HttpClients.createDefault()).thenReturn(mock);
//
//    HttpContentProvider prov = new HttpContentProvider();
//    Assert.assertTrue(prov.canProvide("http"));
//    Assert.assertTrue(prov.canProvide("https"));
//    Assert.assertFalse(prov.canProvide("file"));
//
//    //set status to OK
//    mockHttpClient(HttpStatus.OK);
//
//    ResponseEntity e = prov.provide(URI.create("http://www.google.com"), MediaType.TEXT_HTML, "index.html");
//    //expect HTTP SEE_OTHER and Location header
//    Assert.assertEquals(HttpStatus.SEE_OTHER, e.getStatusCode());
//    Assert.assertEquals("http://www.google.com", e.getHeaders().get("Location").get(0));
//
//    //set status to NOT_FOUND
//    mockHttpClient(HttpStatus.NOT_FOUND);
//    //expecting HTTP 404 internally translated to NO_CONTENT and Content-Location header
//    e = prov.provide(URI.create("http://www.unknown.host"), MediaType.TEXT_HTML, "index.html");
//
//    Assert.assertEquals(HttpStatus.NO_CONTENT, e.getStatusCode());
//    Assert.assertEquals("http://www.unknown.host", e.getHeaders().get("Content-Location").get(0));
//
//    mockHttpClient(HttpStatus.SEE_OTHER);
//    //expecting INTERNAL_SERVER_ERROR as SEE_OTHER is returned but no Location header is present
//    e = prov.provide(URI.create("http://www.google.com"), MediaType.TEXT_HTML, "index.html");
//    Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
//
//    //go through all status codes that are returned transparently with Location header
//    for(HttpStatus status : new HttpStatus[]{HttpStatus.SEE_OTHER, HttpStatus.FOUND, HttpStatus.MOVED_PERMANENTLY, HttpStatus.TEMPORARY_REDIRECT, HttpStatus.PERMANENT_REDIRECT}){
//      mockHttpClient(status, "http://www.google.com");
//
//      e = prov.provide(URI.create("http://www.google.com"), MediaType.TEXT_HTML, "index.html");
//      Assert.assertEquals(status, e.getStatusCode());
//      Assert.assertEquals("http://www.google.com", e.getHeaders().get("Location").get(0));
//    }
//
//    //simulate invalid status code
//    mockHttpClient(null);
//    e = prov.provide(URI.create("http://www.google.com"), MediaType.TEXT_HTML, "index.html");
//    Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, e.getStatusCode());
//    Assert.assertEquals("http://www.google.com", e.getHeaders().get("Content-Location").get(0));
//
//    //test IOException during GET
//    mockFailingHttpClient();
//    e = prov.provide(URI.create("http://www.google.com"), MediaType.TEXT_HTML, "index.html");
//    Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, e.getStatusCode());
//    Assert.assertEquals("http://www.google.com", e.getHeaders().get("Content-Location").get(0));
  }

  private void mockFailingHttpClient() throws Exception{
    PowerMockito.when(mock.execute(Mockito.any(HttpGet.class))).thenThrow(new IOException("This should fail for testing reasons."));
  }

  private void mockHttpClient(HttpStatus expectedStatus) throws Exception{
    mockHttpClient(expectedStatus, null);
  }

  private void mockHttpClient(HttpStatus expectedStatus, String location) throws Exception{
    PowerMockito.when(response.getStatusLine()).thenReturn(line);
    if(location != null){
      PowerMockito.when(header.getName()).thenReturn("Location");
      PowerMockito.when(header.getValue()).thenReturn(location);
      PowerMockito.when(response.getFirstHeader("Location")).thenReturn(header);
    } else{
      PowerMockito.when(response.getFirstHeader("Location")).thenReturn(null);
    }

    if(expectedStatus != null){
      PowerMockito.when(line.getStatusCode()).thenReturn(expectedStatus.value());
    } else{
      PowerMockito.when(line.getStatusCode()).thenReturn(12345);
    }

    PowerMockito.when(mock.execute(Mockito.any(HttpGet.class))).thenReturn(response);
  }
}
