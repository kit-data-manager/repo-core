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

import edu.kit.datamanager.entities.RepoServiceRole;
import edu.kit.datamanager.entities.RepoUserRole;
import edu.kit.datamanager.exceptions.InvalidAuthenticationException;
import edu.kit.datamanager.exceptions.UnauthorizedAccessException;
import edu.kit.datamanager.security.filter.JwtAuthenticationFilter;
import edu.kit.datamanager.security.filter.JwtAuthenticationProvider;
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.security.filter.JwtEmptyToken;
import edu.kit.datamanager.security.filter.JwtUserToken;
import edu.kit.datamanager.util.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author jejkal
 */
public class JwtAuthenticationFilterTest{

  private final AuthenticationManager authenticationManager = PowerMockito.mock(AuthenticationManager.class);
  private final HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
  private final HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
  private final FilterChain filterChain = PowerMockito.mock(FilterChain.class);
  private final SecurityContext context = PowerMockito.mock(SecurityContext.class);

  @Test
  public void testNoAuthenticationHeader() throws Exception{
    Mockito.when(request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER)).thenReturn(null);
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
    filter.doFilter(request, response, filterChain);
    //expecting filterChain to be invoked
    Mockito.verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNoBearerToken() throws Exception{
    Mockito.when(request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER)).thenReturn("test123");

    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
    filter.doFilter(request, response, filterChain);
    //expecting filterChain to be invoked
    Mockito.verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testInvalidBearerToken() throws Exception{
    Mockito.when(request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer test123");

    doAnswer((Answer) new Answer(){
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable{
        return new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationFilterTest.class)).authenticate(new JwtEmptyToken("test123"));
      }
    }).when(authenticationManager).authenticate(any(Authentication.class));

    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
    filter.doFilter(request, response, filterChain);
    //expecting filterChain to be invoked
    Mockito.verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void testValidJwtToken() throws Exception{
//create new token for user 'test123' with ADMINISTRATOR role in group USERS which expires in one hour
    final String token = JwtBuilder.createUserToken("test123", RepoUserRole.ADMINISTRATOR).addSimpleClaim("groupid", "USERS").getCompactToken("test123", DateUtils.addHours(new Date(), 1));

    doAnswer((Answer) new Answer(){
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable{
        return new JwtAuthenticationProvider("test123", null).authenticate(new JwtEmptyToken(token));
      }
    }).when(authenticationManager).authenticate(any(Authentication.class));

    doAnswer((Answer) new Answer(){
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable{
        //validate token elements
        Authentication answer = (Authentication) invocation.getArguments()[0];

        Assert.assertTrue(answer instanceof JwtAuthenticationToken);
        Assert.assertTrue(answer instanceof JwtUserToken);
        Assert.assertEquals("USERS", ((JwtAuthenticationToken) answer).getGroupId());
        Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.USER, ((JwtUserToken) answer).getTokenType());
        Assert.assertEquals("test123", ((JwtUserToken) answer).getPrincipal());
        Assert.assertTrue(((JwtUserToken) answer).getAuthorities().contains(new SimpleGrantedAuthority(RepoUserRole.ADMINISTRATOR.getValue())));

        DefaultClaims claims = (DefaultClaims) Jwts.parser().setSigningKey("test123").parse(((JwtUserToken) answer).getToken()).getBody();

        Assert.assertTrue(claims.containsKey("tokenType"));
        Assert.assertTrue(claims.containsKey("groupid"));
        Assert.assertTrue(claims.containsKey("username"));
        Assert.assertTrue(claims.containsKey("roles"));
        Assert.assertTrue(claims.containsKey("exp"));
        Assert.assertTrue(claims.get("exp", Date.class).before(DateUtils.addHours(new Date(), 1)));
        return null;
      }
    }).when(context).setAuthentication(any(Authentication.class));
    SecurityContextHolder.setContext(context);

    Mockito.when(request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer " + token);
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
    filter.doFilter(request, response, filterChain);
  }

  @Test
  public void testUnauthorizedAccessDetected() throws Exception{
    //create new token for user 'test123' with ADMINISTRATOR role in group USERS which expires in one hour
    final String token = JwtBuilder.createUserToken("test123", RepoUserRole.ADMINISTRATOR).addSimpleClaim("groupid", "USERS").getCompactToken("test123", DateUtils.addHours(new Date(), 1));

    doAnswer((Answer) new Answer(){
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable{
        //throw exception just for fun
        throw new UnauthorizedAccessException("Exception for testing purposes.");
      }
    }).when(authenticationManager).authenticate(any(Authentication.class));

    when(response.getWriter()).thenReturn(new PrintWriter(System.out));

    Mockito.when(request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer " + token);
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
    filter.doFilter(request, response, filterChain);

    //we assume that response status is set to UNAUTHORIZED
    Mockito.verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  public void testAllowedServiceToken(){
    final String token = JwtBuilder.createServiceToken("MyService", RepoServiceRole.SERVICE_READ).addSimpleClaim("sources", "[\"localhost\"]").getCompactToken("test123");
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
    new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationFilterTest.class)).getJwtAuthentication(token);
  }

  @Test(expected = InvalidAuthenticationException.class)
  public void testUnallowedServiceToken(){
    final String token = JwtBuilder.createServiceToken("MyService", RepoServiceRole.SERVICE_READ).addSimpleClaim("sources", "[\"google.com\"]").getCompactToken("test123");
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
    new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationFilterTest.class)).getJwtAuthentication(token);
  }

}
