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
import edu.kit.datamanager.exceptions.AccessForbiddenException;
import edu.kit.datamanager.exceptions.BadArgumentException;
import edu.kit.datamanager.exceptions.EtagMismatchException;
import edu.kit.datamanager.exceptions.EtagMissingException;
import edu.kit.datamanager.exceptions.UnauthorizedAccessException;
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.util.ControllerUtils;
import java.net.InetAddress;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author jejkal
 */
public class ControllerUtilsTest{

  SecurityContext securityContext = Mockito.mock(SecurityContext.class);

  @Test
  public void testCheckPaginationInformation(){
    PageRequest request = PageRequest.of(0, 10, Sort.unsorted());
    request = ControllerUtils.checkPaginationInformation(request);

    Assert.assertEquals(0, request.getPageNumber());
    Assert.assertEquals(10, request.getPageSize());
    Assert.assertEquals(Sort.unsorted(), request.getSort());
  }

  @Test
  public void testCheckPaginationInformationWithTooLargePage(){
    PageRequest request = PageRequest.of(0, 101, Sort.unsorted());
    request = ControllerUtils.checkPaginationInformation(request);

    Assert.assertEquals(0, request.getPageNumber());
    Assert.assertEquals(100, request.getPageSize());
    Assert.assertEquals(Sort.unsorted(), request.getSort());
  }

  @Test
  public void testCheckPaginationInformationWithMaxPageSize(){
    PageRequest request = PageRequest.of(0, 100, Sort.unsorted());
    request = ControllerUtils.checkPaginationInformation(request);

    Assert.assertEquals(0, request.getPageNumber());
    Assert.assertEquals(100, request.getPageSize());
    Assert.assertEquals(Sort.unsorted(), request.getSort());
  }

  @Test
  public void testCheckPaginationInformationWithAlmostMaxPageSize(){
    PageRequest request = PageRequest.of(0, 99, Sort.unsorted());
    request = ControllerUtils.checkPaginationInformation(request);

    Assert.assertEquals(0, request.getPageNumber());
    Assert.assertEquals(99, request.getPageSize());
    Assert.assertEquals(Sort.unsorted(), request.getSort());
  }

  @Test
  public void testCheckPaginationInformationWithTooLargePage1(){
    PageRequest request = PageRequest.of(0, 10, Sort.by("id"));
    request = ControllerUtils.checkPaginationInformation(request);

    Assert.assertEquals(0, request.getPageNumber());
    Assert.assertEquals(10, request.getPageSize());
    Assert.assertEquals(Sort.by("id"), request.getSort());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCheckPaginationInformationWithInvalidPageable(){
    PageRequest request = ControllerUtils.checkPaginationInformation(new Pageable(){
      @Override
      public int getPageNumber(){
        return -1;
      }

      @Override
      public int getPageSize(){
        return 10;
      }

      @Override
      public long getOffset(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public Sort getSort(){
        return Sort.unsorted();
      }

      @Override
      public Pageable next(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public Pageable previousOrFirst(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public Pageable first(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public boolean hasPrevious(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    });

    Assert.fail("Test should have already failed but created page request " + request);
  }

  @Test(expected = UnauthorizedAccessException.class)
  public void testCheckAnonymousTrue(){
    Mockito.when(securityContext.getAuthentication()).thenReturn(null);
    SecurityContextHolder.setContext(securityContext);

    ControllerUtils.checkAnonymousAccess();
  }

  @Test
  public void testCheckAnonymousFalse(){
    JwtAuthenticationToken userToken = edu.kit.datamanager.util.JwtBuilder.
            createUserToken("tester", RepoUserRole.USER).
            addSimpleClaim("firstname", "test").
            addSimpleClaim("lastname", "user").
            addSimpleClaim("email", "test@mail.org").
            addSimpleClaim("groupid", "USERS").
            getJwtAuthenticationToken("test123");
    Mockito.when(securityContext.getAuthentication()).thenReturn(userToken);
    SecurityContextHolder.setContext(securityContext);
    ControllerUtils.checkAnonymousAccess();
  }

  @Test
  public void testCheckAdministratorAccessTrue(){
    JwtAuthenticationToken userToken = edu.kit.datamanager.util.JwtBuilder.
            createUserToken("tester", RepoUserRole.ADMINISTRATOR).
            addSimpleClaim("firstname", "test").
            addSimpleClaim("lastname", "user").
            addSimpleClaim("email", "test@mail.org").
            addSimpleClaim("groupid", "USERS").
            getJwtAuthenticationToken("test123");
    Mockito.when(securityContext.getAuthentication()).thenReturn(userToken);
    SecurityContextHolder.setContext(securityContext);

    ControllerUtils.checkAdministratorAccess();
  }

  @Test(expected = AccessForbiddenException.class)
  public void testCheckAdministratorAccessFalse(){
    JwtAuthenticationToken userToken = edu.kit.datamanager.util.JwtBuilder.
            createUserToken("tester", RepoUserRole.USER).
            addSimpleClaim("firstname", "test").
            addSimpleClaim("lastname", "user").
            addSimpleClaim("email", "test@mail.org").
            addSimpleClaim("groupid", "USERS").
            getJwtAuthenticationToken("test123");
    Mockito.when(securityContext.getAuthentication()).thenReturn(userToken);
    SecurityContextHolder.setContext(securityContext);
    ControllerUtils.checkAdministratorAccess();
  }

  @Test
  public void testCheckETagNotModified(){
    ControllerUtils.checkEtag(createDummyWebRequest(), () -> "1234");
  }

  @Test(expected = EtagMismatchException.class)
  public void testCheckETagModified(){
    ControllerUtils.checkEtag(createDummyWebRequest(), () -> "12343");
  }

  @Test(expected = EtagMissingException.class)
  public void testCheckETagMissing(){
    ControllerUtils.checkEtag(createDummyWebRequest(false), () -> "12343");
  }

  @Test
  public void testGetLocalHostname() throws Exception{
    //  PowerMockito.when(Inet4Address.class, "getHostName").thenReturn("localhost");
    InetAddress a = InetAddress.getByName("localhost");

    PowerMockito.whenNew(InetAddress.class).withAnyArguments().thenReturn(a);

    System.out.println(ControllerUtils.getLocalHostname());
  }

  @Test
  public void testParseIdToLong(){
    Long id = ControllerUtils.parseIdToLong("1");
    Assert.assertEquals(Long.valueOf(1l), id);
  }

  @Test(expected = BadArgumentException.class)
  public void testParseIdToLongFailing(){
    Long id = ControllerUtils.parseIdToLong("ab");
    Assert.fail("Parsing should have been failed before but returned " + id);
  }

  private WebRequest createDummyWebRequest(){
    return createDummyWebRequest(true);
  }

  private WebRequest createDummyWebRequest(final boolean hasEtag){
    return new WebRequest(){
      @Override
      public String getHeader(String headerName){
        if(hasEtag && "If-Match".equals(headerName)){
          return "\"1234\"";
        }
        return null;
      }

      @Override
      public String[] getHeaderValues(String headerName){
        return null;
      }

      @Override
      public Iterator<String> getHeaderNames(){
        return null;
      }

      @Override
      public String getParameter(String paramName){
        return null;
      }

      @Override
      public String[] getParameterValues(String paramName){
        return null;
      }

      @Override
      public Iterator<String> getParameterNames(){
        return null;
      }

      @Override
      public Map<String, String[]> getParameterMap(){
        return null;
      }

      @Override
      public Locale getLocale(){
        return null;
      }

      @Override
      public String getContextPath(){
        return null;
      }

      @Override
      public String getRemoteUser(){
        return null;
      }

      @Override
      public Principal getUserPrincipal(){
        return null;
      }

      @Override
      public boolean isUserInRole(String role){
        return false;
      }

      @Override
      public boolean isSecure(){
        return false;
      }

      @Override
      public boolean checkNotModified(long lastModifiedTimestamp){
        return true;
      }

      @Override
      public boolean checkNotModified(String etag){
        return hasEtag && "1234".equals(etag);
      }

      @Override
      public boolean checkNotModified(String etag, long lastModifiedTimestamp){
        return hasEtag && "1234".equals(etag);
      }

      @Override
      public String getDescription(boolean includeClientInfo){
        return null;
      }

      @Override
      public Object getAttribute(String name, int scope){
        return null;
      }

      @Override
      public void setAttribute(String name, Object value, int scope){
      }

      @Override
      public void removeAttribute(String name, int scope){
      }

      @Override
      public String[] getAttributeNames(int scope){
        return null;
      }

      @Override
      public void registerDestructionCallback(String name, Runnable callback, int scope){
      }

      @Override
      public Object resolveReference(String key){
        return null;
      }

      @Override
      public String getSessionId(){
        return null;
      }

      @Override
      public Object getSessionMutex(){
        return null;
      }
    };
  }
}
