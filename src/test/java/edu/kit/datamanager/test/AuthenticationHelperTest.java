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

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kit.datamanager.entities.PERMISSION;
import edu.kit.datamanager.entities.RepoServiceRole;
import edu.kit.datamanager.entities.RepoUserRole;
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.security.filter.ScopedPermission;
import edu.kit.datamanager.util.AuthenticationHelper;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jejkal
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationHelperTest{

  SecurityContext securityContext = Mockito.mock(SecurityContext.class);

  @Test
  public void testJwtUserToken() throws JsonProcessingException{
    mockJwtUserAuthentication();

    Assert.assertTrue(AuthenticationHelper.hasAuthority(RepoUserRole.ADMINISTRATOR.getValue()));
    Assert.assertEquals("test", AuthenticationHelper.getFirstname());
    Assert.assertEquals("user", AuthenticationHelper.getLastname());
    Assert.assertEquals("tester", AuthenticationHelper.getAuthorizationIdentities().get(0));
    Assert.assertEquals("USERS", AuthenticationHelper.getAuthorizationIdentities().get(1));
    Assert.assertFalse(AuthenticationHelper.isAuthenticatedAsService());
    Assert.assertEquals(PERMISSION.NONE, AuthenticationHelper.getScopedPermission(String.class.getSimpleName(), "1"));
  }

  @Test
  public void testOtherAuthentication(){
    mockNoAuthentication();

    Assert.assertNull(AuthenticationHelper.getFirstname());
    Assert.assertNull(AuthenticationHelper.getLastname());
    Assert.assertEquals("anonymous", AuthenticationHelper.getPrincipal());
  }

  @Test
  public void testJwtServiceToken() throws JsonProcessingException{
    mockJwtServiceAuthentication();

    Assert.assertEquals("metadata_extractor", AuthenticationHelper.getPrincipal());
    Assert.assertTrue(AuthenticationHelper.hasIdentity("metadata_extractor"));
    Assert.assertTrue(AuthenticationHelper.hasAuthority(RepoServiceRole.SERVICE_READ.getValue()));
    Assert.assertTrue(AuthenticationHelper.isAuthenticatedAsService());

  }

  @Test
  public void testJwtTemporaryToken() throws JsonProcessingException{
    mockJwtTemporaryAuthentication();

    Assert.assertEquals("test@mail.org", AuthenticationHelper.getPrincipal());
    Assert.assertTrue(AuthenticationHelper.hasIdentity("test@mail.org"));
    Assert.assertEquals(PERMISSION.READ, AuthenticationHelper.getScopedPermission(String.class.getSimpleName(), "1"));
  }

  private void mockNoAuthentication(){
    Mockito.when(securityContext.getAuthentication()).thenReturn(new AnonymousAuthenticationToken("test", "anonymous", Arrays.asList(new SimpleGrantedAuthority("anonymous"))));
    SecurityContextHolder.setContext(securityContext);
  }

  private void mockJwtUserAuthentication() throws JsonProcessingException{
    JwtAuthenticationToken userToken = edu.kit.datamanager.util.JwtBuilder.
            createUserToken("tester", RepoUserRole.ADMINISTRATOR).
            addSimpleClaim("firstname", "test").
            addSimpleClaim("lastname", "user").
            addSimpleClaim("email", "test@mail.org").
            addSimpleClaim("groupid", "USERS").
            getJwtAuthenticationToken("test123");

    Mockito.when(securityContext.getAuthentication()).thenReturn(userToken);
    SecurityContextHolder.setContext(securityContext);
  }

  private void mockJwtServiceAuthentication() throws JsonProcessingException{
    JwtAuthenticationToken serviceToken = edu.kit.datamanager.util.JwtBuilder.
            createServiceToken("metadata_extractor", RepoServiceRole.SERVICE_READ).
            addSimpleClaim("groupid", "USERS").
            getJwtAuthenticationToken("test123");
    Mockito.when(securityContext.getAuthentication()).thenReturn(serviceToken);
    SecurityContextHolder.setContext(securityContext);
  }

  private void mockJwtTemporaryAuthentication() throws JsonProcessingException{
    ScopedPermission[] perms = new ScopedPermission[]{ScopedPermission.factoryScopedPermission("String", "1", PERMISSION.READ)};

    JwtAuthenticationToken temporaryToken = edu.kit.datamanager.util.JwtBuilder.createTemporaryToken("test@mail.org", perms).
            getJwtAuthenticationToken("test123");
    Mockito.when(securityContext.getAuthentication()).thenReturn(temporaryToken);
    SecurityContextHolder.setContext(securityContext);
  }
}
