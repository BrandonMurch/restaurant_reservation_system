/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.OpeningClosingPair;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
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
import org.springframework.beans.factory.annotation.Autowired;


public class BookingTimes implements Serializable {

  private static final long serialVersionUID = -3114210963113290649L;
  private long id;
  // false = bookingTimesByDay, true = bookingTimes;
  private boolean bookingsAtCertainTimes;
  private List<LocalTime> bookingTimes;
  private Map<DayOfWeek, List<LocalTime>> bookingTimesByDay;
  private Duration bookingSlotIntervals;
  private Map<DayOfWeek, Day> openingHours;
  @Autowired
  private TableAllocatorService tableAllocator;
  @Autowired
  private RestaurantConfig config;

  public BookingTimes() {
    initOpeningHours();
  }

  public BookingTimes(List<LocalTime> bookingTimes) {
    allowBookingsOnlyAtCertainTimes(bookingTimes);
    initOpeningHours();
  }

  public BookingTimes(int minutesBetweenBookingSlots) {
    allowBookingPerTimeInterval(minutesBetweenBookingSlots);
    initOpeningHours();
  }

  private void initOpeningHours() {
    this.openingHours = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      this.openingHours.put(day, new Day(day, false));
    }
  }

  public Map<DayOfWeek, Day> getOpeningHours() {
    return openingHours;
  }

  public void setOpeningHours(Map<DayOfWeek, Day> openingHours) {
    this.openingHours = openingHours;
  }

  public Day getOpeningHours(DayOfWeek dayOfWeek) {
    return openingHours.get(dayOfWeek);
  }

  public boolean isOpenOnDate(LocalDate date) {
    Day day = openingHours.get(date.getDayOfWeek());
    return day.isOpen();
  }

  public void allowBookingsOnlyAtCertainTimes(List<LocalTime> bookingTimes) {
    this.bookingsAtCertainTimes = true;
    this.bookingTimes = new ArrayList<>(bookingTimes);
  }

  public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
    bookingsAtCertainTimes = false;
    this.bookingSlotIntervals =
        Duration.ofMinutes(bookingIntervalInMinutes);
    calculateBookingTimes();
  }

  private void calculateBookingTimes() {
    Map<DayOfWeek, List<LocalTime>> bookingTimesByDay = new HashMap<>();
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      Day day = openingHours.get(dayOfWeek);
      if (day.isOpen()) {
        List<LocalTime> bookingTimes = new ArrayList<>();
        List<OpeningClosingPair> openClosePairs = day.getOpeningPairs();
        for (OpeningClosingPair pair : openClosePairs) {
          LocalTime time = pair.getOpening();
          LocalTime closing = pair.getClosing();
          while (time.isBefore(closing)) {
            bookingTimes.add(time);
            time = time.plus(bookingSlotIntervals);
          }
        }

        bookingTimesByDay.put(dayOfWeek, bookingTimes);
      }
    }
    this.bookingTimesByDay = bookingTimesByDay;

  }

  public boolean isBookingTime(LocalDateTime dateTime) {
    return getAll(
        dateTime.toLocalDate()).contains(dateTime.toLocalTime()
    );
  }

  public SortedSet<LocalTime> getAvailable(int size, LocalDate date) {
    List<LocalTime> times = getAll(date);
    SortedSet<LocalTime> availableTimes = new TreeSet<>();

    for (LocalTime time : times) {
      if (date.isEqual(LocalDate.now()) && time.isBefore(
          LocalTime.now())) {
        continue;
      }
      LocalDateTime dateTime = date.atTime(time);

      if (tableAllocator.getAvailableTable(dateTime, size,
          config.canABookingOccupyALargerTable()).isPresent()) {
        availableTimes.add(time);
      }
    }
    return availableTimes;
  }

  public List<LocalTime> getAll(LocalDate date) {
    Day day = openingHours.get(date.getDayOfWeek());
    if (!day.isOpen()) {
      return Collections.emptyList();
    } else if (bookingsAtCertainTimes) {
      return this.bookingTimes;
    }

    return bookingTimesByDay.get(date.getDayOfWeek());
  }


}
