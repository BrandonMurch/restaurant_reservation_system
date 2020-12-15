/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenUtilTest {

  private final JwtTokenUtil tokenUtil = new JwtTokenUtil();
  private final String username = "user";
  private final User user = new User(username, "pass", new ArrayList<>());
  private final String address = "192.168.0.1";
  private String token;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(tokenUtil, "secret", "supersecret");
    token = tokenUtil.generateToken(user, address);
  }

  @Test
  void getUsernameFromToken() {
    String username = tokenUtil.getUsernameFromToken(token);
    assertEquals(this.username, username);
  }

  @Test
  void getExpirationDateFromToken() {
    Date date = tokenUtil.getExpirationDateFromToken(token);
    System.out.println(date);
    assertTrue(date.after(new Date()));
  }

  @Test
  void getIpAddressFromToken() {
    String address = tokenUtil.getIpAddressFromToken(token);
    assertEquals(this.address, address);
  }

  @Test
  void generateToken() {
    assertFalse(token.isEmpty());
  }

  @Test
  void validateToken() {
    Boolean isValid = tokenUtil
        .validateTokenWithUser(token, user, address);
    assertTrue(isValid);
  }

  @Test
  void checkToken() {
    Boolean result = tokenUtil.validateToken(createFakeToken(), address);
    assertFalse(result);
    result = tokenUtil.validateToken(token, address);
    assertTrue(result);
    result = tokenUtil.validateToken(token, "not an address");
    assertFalse(result);

  }

  private String createFakeToken() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("ip_address", address);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject("user")
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 100 * 1000))
        .signWith(SignatureAlgorithm.HS512, "Not the right secret!")
        .compact();
  }
}