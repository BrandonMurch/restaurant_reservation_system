/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
          .validateToken(token, user, address);
        assertTrue(isValid);
    }
}