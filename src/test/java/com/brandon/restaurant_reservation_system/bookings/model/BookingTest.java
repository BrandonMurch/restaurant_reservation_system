/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingTest {

  private final User user = new User();
  private Booking booking;
  private LocalDateTime start;
  private LocalDateTime end;

  @BeforeEach
  void setUp() {
    start = LocalDateTime.now();
    end = LocalDateTime.now().plusHours(2);
    booking = new Booking(
        2,
        start,
        end,
        user
    );
  }

  @Test
  void updateBooking() {
    Booking newBooking = new Booking(
        4,
        start.minusMonths(1),
        end.minusMonths(1),
        user
    );

    booking.update(newBooking);
    assertEquals(newBooking.getStartTime(), booking.getStartTime());
    assertEquals(newBooking.getEndTime(), booking.getEndTime());
    assertEquals(newBooking.getPartySize(), booking.getPartySize());
  }

  @Test
  void doTheseBookingsOverlap() {
    Booking bookingDoesOverlap = new Booking(
        4,
        start,
        end,
        user
    );

    Booking bookingDoesOverlap2 = new Booking(
        4,
        start.minusHours(1),
        end.plusHours(1),
        user
    );

    Booking bookingDoesOverlap3 = new Booking(
        4,
        start.minusHours(1),
        end.minusHours(1),
        user
    );
    Booking bookingDoesNotOverlap = new Booking(
        4,
        start.minusHours(5),
        start.minusHours(3),
        user
    );
    Booking bookingDoesNotOverlap2 = new Booking(
        4,
        start.minusHours(2),
        start,
        user
    );

    Booking bookingDoesNotOverlap3 = new Booking(
        4,
        start.minusDays(1),
        end.minusDays(1), user
    );

    assertTrue(booking.doesOverlap(bookingDoesOverlap));
    assertTrue(booking.doesOverlap(bookingDoesOverlap2));
    assertTrue(booking.doesOverlap(bookingDoesOverlap3));
    assertFalse(booking.doesOverlap(bookingDoesNotOverlap));
    assertFalse(booking.doesOverlap(bookingDoesNotOverlap2));
    assertFalse(booking.doesOverlap(bookingDoesNotOverlap3));
  }

  @Test
  void isTheBookingDuringThisTime() {
    boolean result = booking.isTheBookingDuringThisTime(
        start,
        end);
    boolean result2 = booking.isTheBookingDuringThisTime(
        start.minusDays(1),
        end.minusDays(1));

    assertTrue(result);
    assertFalse(result2);

  }

  @Test
  void bookingSetTables() {
    Booking booking = CreateBookingsForTest.createUpdatedBookingForFour();
    RestaurantTable table = new RestaurantTable("test", 2, 0);
    booking.setTables(table);
    assertEquals(Collections.singletonList(table), booking.getTables());
  }


  @Test
  void equals() {
    Booking equalBooking = new Booking(
        2,
        start, end,
        user
    );

    assertEquals(booking, booking);
    assertEquals(booking, equalBooking);
    assertNotEquals(booking, new Booking());
  }
}