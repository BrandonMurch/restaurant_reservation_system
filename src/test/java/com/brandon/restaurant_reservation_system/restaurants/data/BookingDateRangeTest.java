/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDateRangeTest {
    final LocalDate start = LocalDate.now();
    LocalDate end = LocalDate.now().plusDays(5);
    private final BookingDateRange bookingDateRange = new BookingDateRange(5);
    private final BookingDateRange bookingDateRangeFixed =
      new BookingDateRange(new DateRange(start, end));

    @Test
    void getBookingRange() {
        DateRange range = bookingDateRange.getBookingRange();
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());

        range = bookingDateRangeFixed.getBookingRange();
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());
    }

    @Test
    void setBookingRange() {
        end = start.plusDays(10);
        bookingDateRange.setBookingRange(10);

        DateRange range = bookingDateRange.getBookingRange();
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());

        bookingDateRange.setBookingRange(new DateRange(start, end));
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());
    }
}