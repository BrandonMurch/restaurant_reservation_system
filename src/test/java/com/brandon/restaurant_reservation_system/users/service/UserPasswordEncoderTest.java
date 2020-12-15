/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class UserPasswordEncoderTest {

  private final UserPasswordEncoder passwordEncoder = new UserPasswordEncoder();

  @Test
  void testCreatePasswordHash() {
    String hash = passwordEncoder.encode("Password");
    if (hash == null) {
      fail();
    }
    String[] splitHash = hash.split(":");
    assertEquals(3, splitHash.length);
    assertTrue(splitHash[0].matches("\\d+"));
  }

  @Test
  void testValidatePassword() {
    String hash = passwordEncoder.encode("Password");
    System.out.println(hash);
    assertTrue(passwordEncoder.matches("Password",
        hash));
  }

}
