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
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.datamanager.entities.RepoUserRole;
import edu.kit.datamanager.exceptions.InvalidAuthenticationException;
import edu.kit.datamanager.security.filter.JwtAuthenticationProvider;
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.security.filter.JwtUserToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author jejkal
 */
public class JwtAuthenticationProviderTest{

  @Test
  public void testSupportsJwtAuthenticationToken(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    Assert.assertTrue(provider.supports(JwtAuthenticationToken.class));
  }

  @Test
  public void testSupportsAnonymousAuthenticationToken(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    Assert.assertFalse(provider.supports(AnonymousAuthenticationToken.class));
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationWithUnsupportedAuthentication(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    Assert.assertNotNull(provider.authenticate(new AnonymousAuthenticationToken("test", "user", Arrays.asList(new SimpleGrantedAuthority("anonymous")))));
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationWithNullArgument(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    Assert.assertNotNull(provider.authenticate(null));
  }

  @Test
  public void testAuthenticateWithJwtAuthenticationToken() throws JsonProcessingException{
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    String token = Jwts.builder().
            claim("flag", Boolean.TRUE).
            claim("username", "test").
            claim("firstname", "test").
            claim("lastname", "user").
            claim("groupid", "USERS").
            claim("roles", new ObjectMapper().writeValueAsString(new String[]{"admin", "user"})).
            setExpiration(DateUtils.addHours(new Date(), 1)).
            signWith(SignatureAlgorithm.HS512, "test123").
            compact();

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(token);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
    Assert.assertTrue(auth instanceof JwtAuthenticationToken);
    jwtToken = (JwtAuthenticationToken) auth;
    Assert.assertEquals("test", jwtToken.getPrincipal());
    Assert.assertTrue(jwtToken instanceof JwtUserToken);

    Assert.assertEquals("test", ((JwtUserToken) jwtToken).getFirstname());
    Assert.assertEquals("user", ((JwtUserToken) jwtToken).getLastname());
    Assert.assertEquals("USERS", jwtToken.getGroupId());
    Assert.assertTrue(jwtToken.isAuthenticated());

    Assert.assertTrue(jwtToken.getAuthorities().contains(new SimpleGrantedAuthority("admin")));
    Assert.assertTrue(jwtToken.getAuthorities().contains(new SimpleGrantedAuthority("user")));
  }

  @Test
  public void testAuthenticateWithNoRoles(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    String token = Jwts.builder().
            claim("flag", Boolean.TRUE).
            claim("username", "test").
            claim("firstname", "test").
            claim("lastname", "user").
            claim("activeGroup", "USERS").
            setExpiration(DateUtils.addHours(new Date(), 1)).
            signWith(SignatureAlgorithm.HS512, "test123").
            compact();

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(token);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
    //authorities should contain only one value, which is ROLE_GUEST
    Assert.assertEquals(1, auth.getAuthorities().size());
    Assert.assertEquals(RepoUserRole.GUEST.getValue(), auth.getAuthorities().iterator().next().getAuthority());
  }

  @Test(expected = InvalidAuthenticationException.class)
  public void testAuthenticateWithExpiredToken(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    String token = Jwts.builder().
            claim("flag", Boolean.TRUE).
            claim("username", "test").
            claim("firstname", "test").
            claim("lastname", "user").
            claim("activeGroup", "USERS").
            claim("roles", Arrays.asList("admin", "user")).
            setExpiration(DateUtils.addHours(new Date(), -1)).
            signWith(SignatureAlgorithm.HS512, "test123").
            compact();

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(token);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
  }

  @Test(expected = InvalidAuthenticationException.class)
  public void testAuthenticateWithInvalidRoleClaim(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("test123", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    String token = Jwts.builder().
            claim("flag", Boolean.TRUE).
            claim("username", "test").
            claim("firstname", "test").
            claim("lastname", "user").
            claim("activeGroup", "USERS").
            claim("roles", "admin").
            setExpiration(DateUtils.addHours(new Date(), -1)).
            signWith(SignatureAlgorithm.HS512, "test123").
            compact();

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(token);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticateWithBadSecret(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("invalid", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));
    String token = Jwts.builder().
            claim("flag", Boolean.TRUE).
            claim("username", "test").
            claim("firstname", "test").
            claim("lastname", "user").
            claim("activeGroup", "USERS").
            claim("roles", "admin").
            setExpiration(DateUtils.addHours(new Date(), -1)).
            signWith(SignatureAlgorithm.HS512, "test123").
            compact();

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(token);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticateWithInvalidToken(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("invalid", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken("invalid_token");

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticateWithNullToken(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("invalid", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));

    JwtAuthenticationToken jwtToken = JwtAuthenticationToken.factoryToken(null);

    Authentication auth = provider.authenticate(jwtToken);
    Assert.assertNotNull(auth);
  }

  @Test
  public void testGrantedAuthoritiesCreation(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("invalid", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));

    Set<String> roleSet = new HashSet<>();
    roleSet.add("admin");
    roleSet.add("user");

    List<SimpleGrantedAuthority> authorities = provider.convertRoleListToGrantedAuthorities(roleSet);
    Assert.assertNotNull(authorities);
    Assert.assertFalse(authorities.isEmpty());
    Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("admin")));
    Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("user")));
  }

  @Test
  public void testGrantedAuthoritiesWithNullArgument(){
    JwtAuthenticationProvider provider = new JwtAuthenticationProvider("invalid", LoggerFactory.getLogger(JwtAuthenticationProviderTest.class));

    List<SimpleGrantedAuthority> authorities = provider.convertRoleListToGrantedAuthorities(null);
    Assert.assertNotNull(authorities);
    Assert.assertTrue(authorities.isEmpty());
  }

}
