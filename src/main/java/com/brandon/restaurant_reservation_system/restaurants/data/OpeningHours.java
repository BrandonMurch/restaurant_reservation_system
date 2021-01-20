/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.DateTimePair;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OpeningHours implements Serializable {

  private static final long serialVersionUID = 5359487224165569910L;
  private Map<DayOfWeek, Day> openingHours;

  public OpeningHours() {
    this.openingHours = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      this.openingHours.put(day, new Day(day));
    }
  }

  public OpeningHours(
      Map<DayOfWeek, Day> openingHours) {
    this.openingHours = openingHours;
  }

  public Map<DayOfWeek, Day> get() {
    return openingHours;
  }

  public void set(
      Map<DayOfWeek, Day> openingHours) {
    this.openingHours = openingHours;
  }

  public boolean isOpen(LocalDate date) {
    return isOpen(date.getDayOfWeek());
  }

  public boolean isOpen(DayOfWeek day) {
    return openingHours.get(day).isOpen();
  }

  public List<DateTimePair> getOpenClosePairs(DayOfWeek day) {
    return openingHours.get(day).getOpeningPairs();
  }
}
