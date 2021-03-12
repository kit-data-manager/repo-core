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
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.security.filter.JwtServiceToken;
import edu.kit.datamanager.security.filter.NoAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doAnswer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jejkal
 */
@RunWith(MockitoJUnitRunner.class)
public class NoAuthenticationFilterTest{

  @Test
  public void test() throws Exception{
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    doAnswer((Answer) new Answer(){
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable{
        Authentication answer = (Authentication) invocation.getArguments()[0];
        Assert.assertTrue(answer instanceof JwtAuthenticationToken);
        Assert.assertTrue(answer instanceof JwtServiceToken);
        Assert.assertEquals("USERS", ((JwtAuthenticationToken) answer).getGroupId());
        Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.SERVICE, ((JwtServiceToken) answer).getTokenType());
        Assert.assertEquals(JwtServiceToken.SELF_SERVICE_NAME, ((JwtServiceToken) answer).getPrincipal());
        Assert.assertTrue(((JwtServiceToken) answer).getAuthorities().contains(new SimpleGrantedAuthority(RepoServiceRole.SERVICE_WRITE.getValue())));

        DefaultClaims claims = (DefaultClaims) Jwts.parser().setSigningKey("test123").parse(((JwtServiceToken) answer).getToken()).getBody();

        Assert.assertTrue(claims.containsKey("groupid"));
        Assert.assertTrue(claims.containsKey("tokenType"));
        Assert.assertTrue(claims.containsKey("servicename"));
        Assert.assertTrue(claims.containsKey("roles"));
        Assert.assertTrue(claims.containsKey("exp"));
        Assert.assertTrue(claims.get("exp", Date.class).before(DateUtils.addHours(new Date(), 1)));

        return null;
      }
    }).when(securityContext).setAuthentication(any(Authentication.class));

    //Not needed? Re-used from elsewhere?
    // Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    FilterChain filterChain = Mockito.mock(FilterChain.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    NoAuthenticationFilter filter = new NoAuthenticationFilter("test123", null);

    filter.doFilter(request, response, filterChain);
  }
}
