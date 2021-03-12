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
package edu.kit.datamanager.service.impl;

import edu.kit.datamanager.entities.ContentElement;
import edu.kit.datamanager.service.IContentProvider;
import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 *
 * @author jejkal
 */
@Component
public class HttpContentProvider implements IContentProvider{

  private final static Logger LOGGER = LoggerFactory.getLogger(HttpContentProvider.class);

  @Override
  public void provide(ContentElement contentElement, MediaType mediaType, String filename, HttpServletResponse response){
    if(contentElement.getContentUri() != null){
      final String uriString = contentElement.getContentUri();
      //try to redirect transfer if scheme is not file
      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(contentElement.getContentUri());
      HttpHeaders headers = new HttpHeaders();
      HttpStatus returnedStatus;
      try{
        CloseableHttpResponse httpResponse = client.execute(httpGet);
        HttpStatus responseStatus = HttpStatus.resolve(httpResponse.getStatusLine().getStatusCode());

        if(responseStatus == null){
          LOGGER.warn("Received unknown response status {} while accessing resource URI " + uriString + ". Returning resource URI in Content-Location header and HTTP SERVICE_UNAVAILABLE.", httpResponse.getStatusLine().getStatusCode(), uriString);
          headers.add("Content-Location", uriString);
          returnedStatus = HttpStatus.SERVICE_UNAVAILABLE;
        } else{
          returnedStatus = responseStatus;
          switch(responseStatus){
            case OK: {
              //add location header in order to trigger redirect
              returnedStatus = HttpStatus.SEE_OTHER;
              headers.add("Location", uriString);
              break;
            }
            case SEE_OTHER:
            case FOUND:
            case MOVED_PERMANENTLY:
            case TEMPORARY_REDIRECT:
            case PERMANENT_REDIRECT: {
              Header location = httpResponse.getFirstHeader("Location");
              if(location != null){
                headers.add("Location", location.getValue());
              } else{
                LOGGER.error("Received status {} but no location header.", responseStatus);
                returnedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
              }
              break;
            }
            default: {
              LOGGER.warn("Received status " + responseStatus + ". Returning Content-Location header with value {} and HTTP NO_CONTENT.", uriString);
              returnedStatus = HttpStatus.NO_CONTENT;
              headers.add("Content-Location", uriString);
            }
          }
        }
      } catch(IOException ex){
        LOGGER.error("Failed to resolve content URI " + uriString + ". Sending HTTP SERVICE_UNAVAILABLE.", ex);
        returnedStatus = HttpStatus.SERVICE_UNAVAILABLE;
        headers.add("Content-Location", uriString);
      }
      response.setStatus(returnedStatus.value());
      Set<String> headerKeys = headers.keySet();
      headerKeys.forEach((headerKey) -> {
        headers.get(headerKey).forEach((value) -> {
          response.addHeader(headerKey, value);
        });
      }); //return new ResponseEntity<>(null, headers, returnedStatus);
    } else{
      //return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  @Override
  public boolean canProvide(String schema){
    return "http".equals(schema) || "https".equals(schema);
  }

}
