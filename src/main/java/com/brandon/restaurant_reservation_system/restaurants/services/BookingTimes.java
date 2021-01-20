/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.data.OpeningHours;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.DateTimePair;
import com.brandon.restaurant_reservation_system.tables.service.TableAllocatorService;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BookingTimes implements Serializable {

  private static final long serialVersionUID = -3114210963113290649L;
  // false = bookingTimesByDay, true = bookingTimes;
  private boolean bookingsAtCertainTimes;
  private List<LocalTime> bookingTimes;
  private final Map<DayOfWeek, List<LocalTime>> bookingTimesByDay = new HashMap<>();
  private final Lock lock = new ReentrantLock();
  @Autowired
  private TableAllocatorService tableAllocator;
  private Duration bookingSlotInterval;
  @Autowired
  private RestaurantConfig config;
  @Autowired
  private TableRepository tableRepository;
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private OpeningHours openingHours;

  public BookingTimes() {
  }

  private void calculateBookingTimes() {
    lock.lock();
    bookingTimesByDay.clear();
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      if (openingHours.isOpen(dayOfWeek)) {
        List<LocalTime> bookingTimes = new ArrayList<>();
        List<DateTimePair> openClosePairs = openingHours.getOpenClosePairs(dayOfWeek);
        for (DateTimePair pair : openClosePairs) {
          LocalTime time = pair.getOpening();
          LocalTime closing = pair.getClosing();
          while (time.isBefore(closing)) {
            bookingTimes.add(time);
            time = time.plus(bookingSlotInterval);
          }
        }

        bookingTimesByDay.put(dayOfWeek, bookingTimes);
      }
    }
    lock.unlock();
  }

  public boolean isBookingTime(LocalDateTime dateTime) {
    return getAll(dateTime.toLocalDate())
        .contains(dateTime.toLocalTime());
  }

  public SortedSet<LocalTime> getAvailable(LocalDate date) {
    var times = getAll(date);
    SortedSet<LocalTime> availableTimes = new TreeSet<>();
    int totalTables = tableRepository.countAllSingles();
    int people = 0;
    int tables = 0;
    for (LocalTime time : times) {
      LocalDateTime dateTime = date.atTime(time);
      var bookings = bookingRepository
          .getBookingsDuringTime(dateTime, dateTime.plus(config.getStandardBookingDuration()));
      for (Booking booking : bookings) {
        people += booking.getPartySize();
        tables += booking.getTables().size();
      }
      if (people < config.getCapacity() && tables < totalTables) {
        availableTimes.add(time);
      }
    }
    return availableTimes;
  }

  public SortedSet<LocalTime> getAvailableBySize(int size, LocalDate date) {
    List<LocalTime> times = getAll(date);
    SortedSet<LocalTime> availableTimes = new TreeSet<>();

    for (LocalTime time : times) {
      if (date.isEqual(LocalDate.now()) && time.isBefore(
          LocalTime.now())) {
        continue;
      }
      LocalDateTime dateTime = date.atTime(time);
      if (tableAllocator.getAvailableTable(dateTime, size).isPresent()) {
        availableTimes.add(time);
      }
    }
    return availableTimes;
  }

  public List<LocalTime> getAll(LocalDate date) {
    lock.lock();
    try {
      if (!openingHours.isOpen(date)) {
        return Collections.emptyList();
      } else if (bookingsAtCertainTimes) {
        return this.bookingTimes;
      }

      return bookingTimesByDay.get(date.getDayOfWeek());
    } finally {
      lock.unlock();
    }


  }


  public void setBookingTimes(List<LocalTime> bookingTimes) {
    this.bookingsAtCertainTimes = true;
    this.bookingTimes = bookingTimes;
  }

  public void setBookingSlotInterval(int minutesBetweenBookingSlots) {
    bookingsAtCertainTimes = false;
    this.bookingSlotInterval =
        Duration.ofMinutes(minutesBetweenBookingSlots);
    calculateBookingTimes();
  }
}
