/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.controller;

import com.brandon.restaurant_reservation_system.TestWebSecurityConfig;
import com.brandon.restaurant_reservation_system.security.model.UserRegisterRequest;
import com.brandon.restaurant_reservation_system.security.service.JwtTokenUtil;
import com.brandon.restaurant_reservation_system.security.service.JwtUserDetailsService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(TestWebSecurityConfig.class)
@ActiveProfiles("Test")
class AuthenticationControllerTest {

    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserPasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtTokenUtil tokenUtil;
    @MockBean
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveUser() throws Exception {
        Mockito.when(userDetailsService
          .saveUser(any(UserRegisterRequest.class))).thenReturn(new User());

        mvc.perform(MockMvcRequestBuilders
          .post("/register")
          .content("{\"username\":\"user\", \"password\":\"pass\"}")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    void generateAuthenticationToken() throws Exception {
        var userDetails = new org.springframework.security.core.userdetails.User(
          "user", "pass", new ArrayList<>()
        );

        Mockito
          .when(userDetailsService.loadUserByUsername(any(String.class)))
          .thenReturn(userDetails);
        Mockito
          .when(tokenUtil.generateToken(eq(userDetails), any(String.class)))
          .thenReturn("ThisIsAToken");

        String content = mvc.perform(MockMvcRequestBuilders
          .post("/authenticate")
          .content("{\"username\":\"user\", \"password\":\"pass\"}")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn().getResponse().getContentAsString();

        String expected = "{\"token\":\"ThisIsAToken\"}";

        assertEquals(expected, content);
    }

    @Test
    void generateAuthenticationTokenTestInvalidCredentials() throws Exception {
        Mockito
          .when(authenticationManager
            .authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(BadCredentialsException.class);

        mvc.perform(MockMvcRequestBuilders
          .get("/register")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().is4xxClientError());
    }
}