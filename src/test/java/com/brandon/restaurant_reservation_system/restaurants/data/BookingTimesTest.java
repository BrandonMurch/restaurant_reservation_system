/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingTimesTest {

  BookingTimes bookingTimes;

  @BeforeEach
  void setUp() {
    bookingTimes = new BookingTimes();
    DayOfWeek[] days = {LocalDate.now().getDayOfWeek(),
        LocalDate.now().plusDays(1).getDayOfWeek()};
    Map<DayOfWeek, Day> openingHours = bookingTimes.getOpeningHours();
    for (DayOfWeek dayOfWeek : days) {
      Day day = openingHours.get(dayOfWeek);
      day.setOpen(true);
      day.addOpeningAndClosing(
          LocalTime.of(15, 0, 0),
          LocalTime.of(20, 0, 0)
      );
    }

  }

  @Test
  void initOpeningHours() {
    bookingTimes = new BookingTimes();
    Map<DayOfWeek, Day> map = bookingTimes.getOpeningHours();
    int counter = 0;
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      Day day = map.get(dayOfWeek);
      assertFalse(day.isOpen());
      counter++;
    }
    assertEquals(7, counter);
  }

  @Test
  void isOpenOnDate() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeekToday = today.getDayOfWeek();

    assertTrue(bookingTimes.isOpenOnDate(today));
  }

  @Test
  void allowBookingsOnlyAtCertainTimes() {
    List<LocalTime> list = new ArrayList<>();
    list.add(LocalTime.now());
    list.add(LocalTime.now().plusHours(1));
    list.add(LocalTime.now().plusHours(1));

    bookingTimes.allowBookingsOnlyAtCertainTimes(list);

    List<LocalTime> result = bookingTimes.getBookingTimes(LocalDate.now());

    assertEquals(list, result);
  }

  @Test
  void allowBookingPerTimeInterval() {
    List<LocalTime> expected = new ArrayList<>();
    for (int i = 15; i < 20; i++) {
      for (int j = 0; j <= 59; j += 30) {
        expected.add(LocalTime.of(i, j));
      }
    }

    bookingTimes.allowBookingPerTimeInterval(30);

    List<LocalTime> result = bookingTimes.getBookingTimes(LocalDate.now());

    assertEquals(expected, result);
  }

  @Test
  void isBookingTime() {
    LocalDate today = LocalDate.now();
    LocalDateTime bookingTime = today.atTime(15, 30);
    LocalDateTime bookingTime1 = today.atTime(15, 0);
    LocalDateTime notBookingTime = today.atTime(7, 30);
    LocalDateTime notBookingTime2 = today.atTime(20, 0);

    bookingTimes.allowBookingPerTimeInterval(30);

    assertTrue(bookingTimes.isBookingTime(bookingTime));
    assertTrue(bookingTimes.isBookingTime(bookingTime1));
    assertFalse(bookingTimes.isBookingTime(notBookingTime));
    assertFalse(bookingTimes.isBookingTime(notBookingTime2));
  }
}