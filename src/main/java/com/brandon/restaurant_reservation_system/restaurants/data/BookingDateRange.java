/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public final class BookingDateRange {

  private boolean fixedBookingWindow;
  private int bookingHorizonInDays;
  private DateRange dateRange;

  public BookingDateRange() {
    set(0);
  }

  public BookingDateRange(int bookingHorizonInDays) {
    set(bookingHorizonInDays);
  }

  public BookingDateRange(DateRange bookingDateRange) {
    set(bookingDateRange);
  }

  public void set(int bookingHorizonInDays) {
    this.fixedBookingWindow = false;
    this.bookingHorizonInDays = bookingHorizonInDays;
  }

  public void set(DateRange bookingDateRange) {
    this.fixedBookingWindow = true;
    this.dateRange = bookingDateRange;
  }

  public DateRange get() {
    if (fixedBookingWindow) {
      return dateRange;
    }
    LocalDate today = LocalDate.now();
    LocalDate end = today.plusDays(bookingHorizonInDays);
    return new DateRange(today, end);
  }


}
