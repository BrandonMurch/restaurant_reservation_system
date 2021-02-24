/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.TimePair;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HoursOfOperation implements Serializable {

  private static final long serialVersionUID = 5359487224165569910L;
  private Map<DayOfWeek, Day> days;

  public HoursOfOperation() {
    this.days = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      this.days.put(day, Day.createDay(day, null, 0));
    }
  }

  public Map<DayOfWeek, Day> get() {
    return days;
  }

  public List<LocalTime> getBookingTimes(LocalDate date) {
    return days.get(date.getDayOfWeek()).getBookingTimes();
  }

  public void setOpeningHours(DayOfWeek day, List<TimePair> timePairs) {
    days.computeIfPresent(day, (key, value) -> {
      value.setOpeningHours(timePairs);
      return value;
    });

  }

  public void set(
      Map<DayOfWeek, Day> openingHours) {
    this.days = openingHours;
  }

  public void setTimes(DayOfWeek day, List<LocalTime> times) {
    days.computeIfPresent(day, (key, value) -> {
      value.setBookingTimes(times);
      return value;
    });
  }

  public void setTimes(DayOfWeek day, int interval) {
    days.computeIfPresent(day, (key, value) -> {
      value.setBookingSlotInterval(interval);
      return value;
    });
  }

  public boolean isOpen(LocalDate date) {
    return isOpen(date.getDayOfWeek());
  }

  public boolean isOpen(DayOfWeek day) {
    return days.get(day).isOpen();
  }

  public boolean isBookingTime(LocalDateTime dateTime) {
    return days.get(dateTime.getDayOfWeek()).isOpen(dateTime.toLocalTime());
  }

  public List<TimePair> getOpenClosePairs(DayOfWeek day) {
    return days.get(day).getOpeningPairs();
  }
}
