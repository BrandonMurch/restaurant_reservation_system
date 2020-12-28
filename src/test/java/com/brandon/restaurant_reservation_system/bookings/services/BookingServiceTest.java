/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@Import(TestWebSecurityConfig.class)
@ActiveProfiles("Test")
public class BookingServiceTest {

  @MockBean
  UserPasswordEncoder passwordEncoder;
  @MockBean
  AuthenticationManager authenticationManager;
  @Autowired
  private BookingService service;

  @Test
  void testCreateBookingUpdateCache() throws InterruptedException {
    var bookingsOnDate = new HashMap<>(service.getBookingsPerDate());
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    service.createBooking(booking,
        CreateUsersForTesting.createUser1(), true);
    sleep(1000);
    var result = service.getBookingsPerDate();
    assertEquals(bookingsOnDate.size() + 1, result.size());

  }
}
