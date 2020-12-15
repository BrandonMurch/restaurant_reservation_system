/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopulateRestaurantService {

  public static void populateRestaurant(Restaurant restaurant) {
    setConfig(restaurant);
    restaurant.setBookingDateRange(30);
    restaurant.setOpeningHours(
        modifyOpeningHours(restaurant.getOpeningHours()));
    restaurant.allowBookingPerTimeInterval(15);
  }

  private static void setConfig(Restaurant restaurant) {
    RestaurantConfig config = new RestaurantConfig();
    config.setCapacity(40);
    config.setStandardBookingDuration(120);
    config.setCanABookingOccupyALargerTable(true);
    config.setPeoplePerInterval(0);
    restaurant.setConfig(config);
  }

  public static void populateRestaurantTables(Restaurant restaurant) {
    addTables(restaurant);
    List<RestaurantTable> tables = restaurant.getTableList();
    addCombinations(tables, restaurant);
  }

  private static DateRange getDateRange() {
    LocalDate start = LocalDate.now();
    LocalDate end = LocalDate.of(2020, 8, 31);

    return new DateRange(start, end);
  }

  private static void addTables(Restaurant restaurant) {
    //		restaurant.addTable("k1", 2);
    //		restaurant.addTable("k2", 2);
    //		restaurant.addTable("b1", 2);
    //		restaurant.addTable("b2", 2);
    //		restaurant.addTable("1", 4);
    //		restaurant.addTable("5", 4);
    //		restaurant.addTable("20", 2);
    //		restaurant.addTable("21", 2);
    //		restaurant.addTable("22", 2);
    //		restaurant.addTable("23", 2);
    restaurant.addTable("24", 2);
    restaurant.addTable("25", 2);
  }

  //
  private static void addCombinations(List<RestaurantTable> tables,
      Restaurant restaurant) {

    restaurant.addTableCombination(
        Arrays.asList(tables.get(1), tables.get(0)));
    //		restaurant.addTableCombination(Arrays.asList(tables.get(7),
    //		tables.get(8), tables.get(9)));
    //		restaurant.addTableCombination(Arrays.asList(tables.get(9),
    //		tables.get(8)));
    //		restaurant.addTableCombination(Arrays.asList(tables.get(4),
    //		tables.get(5)));
  }

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
      newMap.computeIfPresent(day, (key, val) -> {
        val.setOpen(true);
        val.addOpeningAndClosing(opening, closing);
        return val;
      });
    }
    return newMap;
  }

  private List<LocalTime> getBookingTimes() {
    return Arrays.asList(LocalTime.of(18, 0),
        LocalTime.of(20, 30),
        LocalTime.of(23, 15));
  }
}
