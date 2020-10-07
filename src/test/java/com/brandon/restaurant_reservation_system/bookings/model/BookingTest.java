/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.model;

import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;
    private final User user = new User();

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

        booking.updateBooking(newBooking);
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

        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap));
        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap2));
        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap3));
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap));
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap2));
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap3));
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