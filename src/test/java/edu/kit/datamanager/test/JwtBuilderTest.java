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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.datamanager.entities.PERMISSION;
import edu.kit.datamanager.entities.RepoServiceRole;
import edu.kit.datamanager.entities.RepoUserRole;
import edu.kit.datamanager.security.filter.JwtAuthenticationToken;
import edu.kit.datamanager.security.filter.JwtServiceToken;
import edu.kit.datamanager.security.filter.JwtTemporaryToken;
import edu.kit.datamanager.security.filter.JwtUserToken;
import edu.kit.datamanager.security.filter.ScopedPermission;
import edu.kit.datamanager.util.JwtBuilder;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jejkal
 */
public class JwtBuilderTest{

  @Test
  public void testServiceToken() throws IOException{
    JwtBuilder builder = JwtBuilder.createServiceToken("myservice", RepoServiceRole.SERVICE_ADMINISTRATOR);
    builder.addSimpleClaim("sources", "[\"localhost\"]");

    Map<String, Object> claimMap = builder.getClaimMap();
    Assert.assertTrue(claimMap.containsKey("servicename"));
    Assert.assertEquals("myservice", claimMap.get("servicename"));
    Assert.assertTrue(claimMap.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.SERVICE.toString(), (String) claimMap.get("tokenType"));
    Assert.assertTrue(claimMap.containsKey("roles"));
    String[] roles = new ObjectMapper().readValue((String) claimMap.get("roles"), String[].class);
    Assert.assertArrayEquals(new String[]{RepoServiceRole.SERVICE_ADMINISTRATOR.getValue()}, roles);

    JwtAuthenticationToken jwtAuthToken = builder.getJwtAuthenticationToken("test123");
    Assert.assertTrue(jwtAuthToken instanceof JwtServiceToken);
    String compactToken = builder.getCompactToken("test123");
    Assert.assertEquals(compactToken, jwtAuthToken.getToken());

    Claims claims = builder.getClaims();
    Assert.assertTrue(claims.containsKey("servicename"));
    Assert.assertEquals("myservice", claims.get("servicename"));
    Assert.assertTrue(claims.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.SERVICE.toString(), (String) claims.get("tokenType"));
    roles = new ObjectMapper().readValue((String) claims.get("roles"), String[].class);
    Assert.assertArrayEquals(new String[]{RepoServiceRole.SERVICE_ADMINISTRATOR.getValue()}, roles);
  }

  @Test
  public void testUserToken() throws IOException{
    JwtBuilder builder = JwtBuilder.createUserToken("tester", RepoUserRole.USER).addSimpleClaim("age", 38).addSimpleClaim("active", Boolean.TRUE);

    Map<String, Object> claimMap = builder.getClaimMap();
    Assert.assertTrue(claimMap.containsKey("username"));
    Assert.assertEquals("tester", claimMap.get("username"));
    Assert.assertTrue(claimMap.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.USER.toString(), (String) claimMap.get("tokenType"));
    Assert.assertTrue(claimMap.containsKey("age"));
    Assert.assertEquals(38, (int) claimMap.get("age"));
    Assert.assertTrue(claimMap.containsKey("active"));
    Assert.assertEquals(true, (boolean) claimMap.get("active"));

    Assert.assertTrue(claimMap.containsKey("roles"));
    String[] roles = new ObjectMapper().readValue((String) claimMap.get("roles"), String[].class);
    Assert.assertArrayEquals(new String[]{RepoUserRole.USER.getValue()}, roles);

    JwtAuthenticationToken jwtAuthToken = builder.getJwtAuthenticationToken("test123");
    Assert.assertTrue(jwtAuthToken instanceof JwtUserToken);
    String compactToken = builder.getCompactToken("test123");
    Assert.assertEquals(compactToken, jwtAuthToken.getToken());

    Claims claims = builder.getClaims();
    Assert.assertTrue(claims.containsKey("username"));
    Assert.assertEquals("tester", claims.get("username"));
    Assert.assertTrue(claims.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.USER.toString(), (String) claims.get("tokenType"));
    Assert.assertTrue(claims.containsKey("age"));
    Assert.assertEquals(38, (int) claims.get("age"));
    Assert.assertTrue(claims.containsKey("active"));
    Assert.assertEquals(true, (boolean) claims.get("active"));
    Assert.assertTrue(claims.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.USER.toString(), (String) claims.get("tokenType"));
    roles = new ObjectMapper().readValue((String) claims.get("roles"), String[].class);
    Assert.assertArrayEquals(new String[]{RepoUserRole.USER.getValue()}, roles);
  }

  @Test
  public void testTemporaryToken() throws IOException{
    JwtBuilder builder = JwtBuilder.createTemporaryToken("test@mail.org", ScopedPermission.factoryScopedPermission("DataResource", "1", PERMISSION.WRITE));

    Map<String, Object> claimMap = builder.getClaimMap();
    Assert.assertTrue(claimMap.containsKey("principalname"));
    Assert.assertEquals("test@mail.org", claimMap.get("principalname"));

    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.TEMPORARY.toString(), (String) claimMap.get("tokenType"));
    Assert.assertTrue(claimMap.containsKey("permissions"));
    ScopedPermission[] permissions = new ObjectMapper().readValue((String) claimMap.get("permissions"), ScopedPermission[].class);
    Assert.assertArrayEquals(new ScopedPermission[]{ScopedPermission.factoryScopedPermission("DataResource", "1", PERMISSION.WRITE)}, permissions);

    JwtAuthenticationToken jwtAuthToken = builder.getJwtAuthenticationToken("test123");
    Assert.assertTrue(jwtAuthToken instanceof JwtTemporaryToken);
    String compactToken = builder.getCompactToken("test123");
    Assert.assertEquals(compactToken, jwtAuthToken.getToken());

    Claims claims = builder.getClaims();
    Assert.assertTrue(claims.containsKey("principalname"));
    Assert.assertEquals("test@mail.org", claims.get("principalname"));
    Assert.assertTrue(claims.containsKey("tokenType"));
    Assert.assertEquals(JwtAuthenticationToken.TOKEN_TYPE.TEMPORARY.toString(), (String) claims.get("tokenType"));
    permissions = new ObjectMapper().readValue((String) claims.get("permissions"), ScopedPermission[].class);
    Assert.assertArrayEquals(new ScopedPermission[]{ScopedPermission.factoryScopedPermission("DataResource", "1", PERMISSION.WRITE)}, permissions);
  }

  @Test
  public void testTemporaryTokenWithInvalidScopedPermissions() throws IOException{
    JwtBuilder builder = JwtBuilder.createTemporaryToken("test@mail.org");
    Assert.assertEquals("[]", builder.getClaimMap().get("permissions"));
    builder = JwtBuilder.createTemporaryToken("test@mail.org", (ScopedPermission[])null);
    Assert.assertEquals("[]", builder.getClaimMap().get("permissions"));
  }
}
