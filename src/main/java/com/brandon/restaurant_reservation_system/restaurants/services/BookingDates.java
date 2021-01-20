/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.data.Cache;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingDates {

  private final AvailableDates availableDates = new AvailableDates();
  @Autowired
  private BookingTimes bookingTimes;
  @Autowired
  private BookingDateRange bookingDateRange;

  public BookingDates() {
  }

  public SortedSet<LocalDate> getAll() {
    return availableDates.get();
  }

  public void add(LocalDate date) {
    availableDates.add(date);
  }

  public void removeDateIfUnavailable(LocalDate date) {
    if (!tryBookingOnDate(date)) {
      availableDates.remove(date);
    }
  }

  public boolean isDateAvailable(LocalDate date) {
    return availableDates.get().contains(date);
  }

  private boolean tryBookingOnDate(LocalDate date) {
    return bookingTimes.getAvailable(date).size() > 0;
  }

  private class AvailableDates extends Cache<SortedSet<LocalDate>> {

    public AvailableDates() {
      super(new TreeSet<>());
    }

    protected SortedSet<LocalDate> update() {
      SortedSet<LocalDate> updatedSet = new TreeSet<>();
      DateRange dates = bookingDateRange.get();
      LocalDate current = dates.getStart();
      LocalDate end = dates.getEnd().plusDays(1);

      while (current.isBefore(end)) {
        if (tryBookingOnDate(current)) {
          updatedSet.add(current);
        }
        current = current.plusDays(1);
      }
      return updatedSet;
    }

    protected void add(LocalDate date) {
      this.handleLock(() -> this.getForUpdate().add(date));
    }

    protected void remove(LocalDate date) {
      handleLock(() -> this.getForUpdate().remove(date));
    }
  }

}
