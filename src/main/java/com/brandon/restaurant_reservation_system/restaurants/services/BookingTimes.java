/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.tables.service.TableAllocatorService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BookingTimes {

  @Autowired
  private TableAllocatorService tableAllocator;
  @Autowired
  private RestaurantConfig config;
  @Autowired
  private TableRepository tableRepository;
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private HoursOfOperation hoursOfOperation;

  public BookingTimes() {
  }

  public SortedSet<LocalTime> getAvailable(LocalDate date) {
    var times = hoursOfOperation.getBookingTimes(date);
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
    List<LocalTime> times = hoursOfOperation.getBookingTimes(date);
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
}
