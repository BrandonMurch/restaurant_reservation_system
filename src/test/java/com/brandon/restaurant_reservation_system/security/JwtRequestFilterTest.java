/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security;

import com.brandon.restaurant_reservation_system.security.service.JwtTokenUtil;
import com.brandon.restaurant_reservation_system.security.service.JwtUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUserDetailsService userDetailsService;
    @Mock
    private JwtTokenUtil tokenUtil;
    @InjectMocks
    private JwtRequestFilter filterUnderTest;
    private MockFilterChain mockFilterChain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @BeforeEach
    void setUp() {
        mockFilterChain = new MockFilterChain();
        request =
          new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void doFilterInternal() throws ServletException, IOException {
        Mockito
          .when(tokenUtil.getUsernameFromToken(any(String.class)))
          .thenReturn("user");
        Mockito
          .when(userDetailsService.loadUserByUsername(any(String.class)))
          .thenReturn(
            new User("user", "pass", new ArrayList<>())
          );
        Mockito
          .when(tokenUtil.validateTokenWithUser(any(String.class), any(User.class),
            any(String.class)))
          .thenReturn(true);

        // token must have Bearer as its first word for the filter to recognize it.
        String token = "Bearer This is a token!";
        request.addHeader("Authorization", token);

        filterUnderTest.doFilterInternal(request, response, mockFilterChain);

        verify(tokenUtil, times(1))
          .getUsernameFromToken(any(String.class));
        verify(tokenUtil, times(1))
          .validateTokenWithUser(any(String.class), any(User.class),
            any(String.class));
        verify(userDetailsService, times(1))
          .loadUserByUsername(any(String.class));
    }
}