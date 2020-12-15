/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.data.LoginableRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceTest {

  @Mock
  private LoginableRepository loginableRepository;
  @InjectMocks
  private JwtUserDetailsService userDetailsService;

  @Test
  void loadUserByUsername() {
    User user = CreateUsersForTesting.createUser1();
    Mockito.when(loginableRepository.findByUsername(any(String.class)))
        .thenReturn(Optional.of(user));
    var userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    assertEquals(user.getUsername(), userDetails.getUsername());
    assertEquals(user.getPassword(), userDetails.getPassword());
  }

  @Test
  void loadUserByUsernameNotFound() {
    Mockito.when(loginableRepository.findByUsername(any(String.class)))
        .thenReturn(Optional.empty());
    Exception exception = assertThrows(UserNotFoundException.class, () ->
        userDetailsService.loadUserByUsername("username"));

    assertTrue(exception.getMessage().contains("User was not found"));
  }
}