/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDateTime;
import static com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest.getTable1;
import static com.brandon.restaurant_reservation_system.users.CreateUsersForTesting.createUser1;

public class CreateBookingsForTest {

    public CreateBookingsForTest() {
    }

    public static Booking createBookingForTwoAt19() {
        Booking booking = new Booking(2,
          parseDateTime("2020-10-09T19:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          parseDateTime("2020-10-09T23:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          createUser1());
        booking.setId(1);
        return booking;
    }

    public static Booking createBookingForFourAt20() {
        Booking booking = new Booking(4,
          parseDateTime("2020-10-09T20:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          parseDateTime("2020-10-09T23:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          createUser1());
        booking.setId(2);
        return booking;
    }

    public static Booking createUpdatedBookingForFour() {
        Booking booking = new Booking(
          4,
          parseDateTime(
            "2020-10-09T20:00:00.00", GlobalVariables.getDateTimeFormat()
          ),
          parseDateTime(
            "2020-10-09T18:00:00.00", GlobalVariables.getDateTimeFormat()
          ),
          createUser1());
        booking.setId(2);
        booking.addTable(getTable1());
        return booking;

    }

    public static Booking createBookingForFourAt19() {
        Booking booking = new Booking(4,
          parseDateTime("2020-10-11T19:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          parseDateTime("2020-10-11T23:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          createUser1());
        booking.setId(3);
        return booking;
    }

    public Booking createBookingOnDifferentDate() {
        Booking booking = new Booking(4,
          parseDateTime("2020-10-10T20:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          parseDateTime("2020-10-10T23:00:00.00",
            GlobalVariables.getDateTimeFormat()),
          createUser1());
        booking.setId(2);
        return booking;
    }
}
