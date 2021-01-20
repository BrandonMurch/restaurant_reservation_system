///*
// * Copyright (c) 2020 Brandon Murch
// */
//
//package com.brandon.restaurant_reservation_system.restaurants.model;
//
//import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
//import com.brandon.restaurant_reservation_system.restaurants.services.BookingTimes;
//import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
//import com.brandon.restaurant_reservation_system.restaurants.exceptions.RestaurantConfigurationException;
//import com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService;
//import com.brandon.restaurant_reservation_system.tables.service.TableService;
//import java.io.Serializable;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class Restaurant implements Serializable {
//
//  private static final long serialVersionUID = 2993992281945949085L;
//
//  @Autowired
//  private transient TableService tableService;
//  private String name;
//  private BookingTimes bookingTimes = new BookingTimes();
//  private transient BookingDateRange bookingDateRange = new BookingDateRange();
//  private RestaurantConfig config;
//
//  public Restaurant() {
//  }
//
//  public Restaurant(String name,
//      RestaurantConfig restaurantConfig,
//      int minutesBetweenBookingSlots) {
//    this(name, restaurantConfig);
//    this.bookingTimes = new BookingTimes(minutesBetweenBookingSlots);
//    serialize();
//  }
//
//  private Restaurant(String name,
//      RestaurantConfig restaurantConfig) {
//    this.name = name;
//    this.config = restaurantConfig;
//    bookingDateRange.set(120);
//  }
//
//  public Restaurant(String name,
//      RestaurantConfig restaurantConfig,
//      List<LocalTime> bookingTimes) {
//    this(name, restaurantConfig);
//    this.bookingTimes = new BookingTimes(bookingTimes);
//    serialize();
//  }
//
//  @PostConstruct
//  private void postConstruct() {
//    // TODO: reinstate this in production
//    //		boolean isDeserializeSuccess = deserialize();
//
//    //		if (!isDeserializeSuccess) {
//    PopulateRestaurantService.populate();
//    //		}
//    // TODO: Remove this when database is created.
//    PopulateRestaurantService.populateTables();
//  }
//
//  // Name --------------------------------------------------------------------
//
//  public String getName() {
//    return name;
//  }
//
////  public void setName(String name) {
////    this.name = name;
////    serialize();
////  }
//
//  // Capacity ----------------------------------------------------------------
//
//  public int getCapacity() {
//    return config.getCapacity();
//  }
//
//  public void setCapacity(int capacity) {
//    config.setCapacity(capacity);
//    serialize();
//  }
//
//  // Config ------------------------------------------------------------------
//
//  public boolean canABookingOccupyALargerTable() {
//    return config.canABookingOccupyALargerTable();
//  }
//
//  public Duration getStandardBookingDuration() {
//    return config.getStandardBookingDuration();
//  }
//
//
//
//  public boolean isBookingTime(LocalDateTime dateTime) {
//    if (bookingTimes == null) {
//      throw new RestaurantConfigurationException("booking times");
//    }
//    return bookingTimes.isBookingTime(dateTime);
//  }
//
//
////  public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
////    bookingTimes.allowBookingPerTimeInterval(bookingIntervalInMinutes);
////    serialize();
////  }
//
//  // Serialization & Deserialization ----------------------------------------
//
//
//
//}
