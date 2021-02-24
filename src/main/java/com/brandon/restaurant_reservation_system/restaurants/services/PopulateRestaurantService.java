/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.tables.service.TableService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PopulateRestaurantService {

  @Autowired
  private RestaurantConfig config;
  @Autowired
  private HoursOfOperation hoursOfOperation;
  @Autowired
  private BookingDateRange dateRange;
  @Autowired
  private TableService tables;
  @Autowired
  private BookingTimes bookingTimes;

  private static Map<DayOfWeek, Day> modifyOpeningHours(
      Map<DayOfWeek, Day> map) {
    Map<DayOfWeek, Day> newMap = new HashMap<>(map);

    DayOfWeek[] days = {
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    };

    for (DayOfWeek day : days) {
      LocalTime opening = LocalTime.of(18, 0);
      LocalTime closing = LocalTime.of(23, 20);
      newMap.computeIfPresent(day, (key, value) -> {
        value.addOpeningAndClosing(opening, closing);
        value.setBookingSlotInterval(15);
        return value;
      });
    }
    return newMap;
  }

  public void populateTables() {
    addTables();
    addCombinations();
  }

  private void addTables() {
    tables.add("k1", 2);
    tables.add("k2", 2);
    tables.add("b1", 2);
    tables.add("b2", 2);
    tables.add("1", 4);
    tables.add("5", 4);
    tables.add("20", 2);
    tables.add("21", 2);
    tables.add("22", 2);
    tables.add("23", 2);
    tables.add("24", 2);
    tables.add("25", 2);
  }

  //
  private void addCombinations() {
    List<RestaurantTable> allTables = tables.findAll();
    tables.createCombination(
        Arrays.asList(allTables.get(1), allTables.get(0)));
    tables.createCombination(Arrays.asList(allTables.get(7),
        allTables.get(8), allTables.get(9)));
    tables.createCombination(Arrays.asList(allTables.get(9),
        allTables.get(8)));
    tables.createCombination(Arrays.asList(allTables.get(4),
        allTables.get(5)));
  }

  public void populate() {
    dateRange.set(30);
    hoursOfOperation.set(
        modifyOpeningHours(hoursOfOperation.get()));
    config.setCapacity(40);
    config.setStandardBookingDuration(120);
    config.setCanABookingOccupyALargerTable(true);
    config.setPeoplePerInterval(0);
  }
}
