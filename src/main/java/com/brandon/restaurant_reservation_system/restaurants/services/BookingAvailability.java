/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.data.Cache;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingTimes;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;

public class BookingAvailability {

  private final AvailableDates availableDates = new AvailableDates();
  @Autowired
  private BookingDateRange bookingDateRange;
  @Autowired
  private BookingTimes bookingTimes;
  @Autowired
  private TableAllocatorService tableAllocatorService;

  public SortedSet<LocalDate> get() {
    return availableDates.get();
  }

  public void add(LocalDate date) {
    availableDates.add(date);
  }

  public void removeDateIfUnavailable(LocalDate date) {
    if (!tryBookingOnDate(date)) {
      availableDates.removeDate(date);
    }
  }

  public boolean isDateAvailable(LocalDate date) {
    if (isClosedOnDate(date)) {
      return false;
    }

    return availableDates.get().contains(date);
  }

  private boolean tryBookingOnDate(LocalDate date) {
    List<LocalTime> times = bookingTimes.getBookingTimes(date);
    for (LocalTime time : times) {
      LocalDateTime dateTime = date.atTime(time);
      if (tableAllocatorService.getAvailableTable(dateTime, 2,
          true).isPresent()) {
        return true;
      }
    }
    return false;
  }

  private boolean isClosedOnDate(LocalDate date) {
    return !bookingTimes.isOpenOnDate(date);
  }

  private class AvailableDates extends Cache<SortedSet<LocalDate>> {

    public AvailableDates() {
      this(new TreeSet<>());
    }

    public AvailableDates(SortedSet<LocalDate> data) {
      super(data);
    }

    public SortedSet<LocalDate> update() {
      SortedSet<LocalDate> updatedSet = new TreeSet<>();
      DateRange dates = bookingDateRange.get();
      LocalDate current = dates.getStart();
      LocalDate end = dates.getEnd().plusDays(1);

      while (current.isBefore(end)) {
        if (bookingTimes.isOpenOnDate(current)
            && tryBookingOnDate(current)) {
          updatedSet.add(current);
        }
        current = current.plusDays(1);
      }
      return updatedSet;
    }

    protected void add(LocalDate date) {
      this.handleLock(() -> this.data.add(date));
    }

    protected void removeDate(LocalDate date) {
      handleLock(() -> this.data.remove(date));
    }
  }

}
