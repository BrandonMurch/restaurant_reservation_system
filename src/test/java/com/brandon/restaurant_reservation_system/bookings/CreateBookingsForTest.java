/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings;

import static com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest.getTable1;
import static com.brandon.restaurant_reservation_system.users.CreateUsersForTesting.createUser1;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import java.time.LocalDate;

public class CreateBookingsForTest {

  private static LocalDate getNextSaturday() {
    LocalDate now = LocalDate.now();
    int daysToAddToBecomeSaturday = 6 - now.getDayOfWeek().getValue();
    return now.plusDays(daysToAddToBecomeSaturday);
  }

  public static Booking createBookingForTwoAt19() {
    Booking booking = new Booking(2,
        getNextSaturday().atTime(19, 0),
        getNextSaturday().atTime(23, 0),
        createUser1());
    booking.setId(1);
    return booking;
  }

  public static Booking createBookingForFourAt20() {
    Booking booking = new Booking(4,
        getNextSaturday().atTime(20, 0),
        getNextSaturday().atTime(23, 0),
        createUser1());
    booking.setId(2);
    return booking;
  }

  public static Booking createUpdatedBookingForFour() {
    Booking booking = new Booking(
        4,
        getNextSaturday().atTime(21, 0),
        getNextSaturday().atTime(23, 0),
        createUser1());
    booking.setId(2);
    booking.addTable(getTable1());
    return booking;

  }

  public static Booking createBookingForFourAt19OnDifferentDate() {
    Booking booking = new Booking(4,
        getNextSaturday().plusWeeks(1).atTime(19, 0),
        getNextSaturday().plusWeeks(1).atTime(23, 0),
        createUser1());
    booking.setId(3);
    return booking;
  }

  public static Booking createBookingOnDifferentDate() {
    Booking booking = new Booking(4,
        getNextSaturday().plusWeeks(1).atTime(20, 0),
        getNextSaturday().plusWeeks(1).atTime(23, 0),
        createUser1());
    booking.setId(2);
    return booking;
  }
}
