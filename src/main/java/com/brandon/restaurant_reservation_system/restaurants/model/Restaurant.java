/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingTimes;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.RestaurantConfigurationException;
import com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableService;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Restaurant implements Serializable {

  private static final long serialVersionUID = 2993992281945949085L;

  @Autowired
  private transient TableService tableService;
  private String name;
  private BookingTimes bookingTimes = new BookingTimes();
  private transient BookingDateRange bookingDateRange = new BookingDateRange(0);
  private RestaurantConfig config;

  public Restaurant() {
  }

  public Restaurant(String name,
      RestaurantConfig restaurantConfig,
      int minutesBetweenBookingSlots) {
    this(name, restaurantConfig);
    this.bookingTimes = new BookingTimes(minutesBetweenBookingSlots);
    serialize();
  }

  private Restaurant(String name,
      RestaurantConfig restaurantConfig) {
    this.name = name;
    this.config = restaurantConfig;
    bookingDateRange = new BookingDateRange(120);
  }

  public Restaurant(String name,
      RestaurantConfig restaurantConfig,
      List<LocalTime> bookingTimes) {
    this(name, restaurantConfig);
    this.bookingTimes = new BookingTimes(bookingTimes);
    serialize();
  }

  @PostConstruct
  private void postConstruct() {
    // TODO: reinstate this in production
    //		boolean isDeserializeSuccess = deserialize();

    //		if (!isDeserializeSuccess) {
    PopulateRestaurantService.populateRestaurant(this, bookingDateRange);
    //		}
    // TODO: Remove this when database is created.
    PopulateRestaurantService.populateRestaurantTables(tableService);
  }

  // Name --------------------------------------------------------------------

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    serialize();
  }

  // Capacity ----------------------------------------------------------------

  public int getCapacity() {
    return config.getCapacity();
  }

  public void setCapacity(int capacity) {
    config.setCapacity(capacity);
    serialize();
  }

  // Config ------------------------------------------------------------------

  public boolean canABookingOccupyALargerTable() {
    return config.canABookingOccupyALargerTable();
  }

  public Duration getStandardBookingDuration() {
    return config.getStandardBookingDuration();
  }

  // Booking times -----------------------------------------------------------

  public Map<DayOfWeek, Day> getOpeningHours() {
    return bookingTimes.getOpeningHours();
  }

  public void setOpeningHours(Map<DayOfWeek, Day> openingHours) {
    bookingTimes.setOpeningHours(openingHours);
    serialize();
  }

  public List<LocalTime> getBookingTimes(LocalDate date) {
    return bookingTimes.getAll(date);
  }

  public boolean isBookingTime(LocalDateTime dateTime) {
    if (bookingTimes == null) {
      throw new RestaurantConfigurationException("booking times");
    }
    return bookingTimes.isBookingTime(dateTime);
  }

  public void setConfig(RestaurantConfig config) {
    this.config = config;
  }

  public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
    bookingTimes.allowBookingPerTimeInterval(bookingIntervalInMinutes);
    serialize();
  }

  // Serialization & Deserialization ----------------------------------------


  private void serialize() {

    try {
      FileOutputStream fileOut = new FileOutputStream("restaurant.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(this);
      out.close();
      fileOut.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean deserialize() {
    try {
      FileInputStream fileIn = new FileInputStream("restaurant.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      Restaurant restaurant = (Restaurant) in.readObject();

      this.name = restaurant.name;
      this.bookingTimes = restaurant.bookingTimes;
      this.bookingDateRange = restaurant.bookingDateRange;
      this.config = restaurant.config;
      in.close();
      fileIn.close();
      return true;
    } catch (IOException | ClassNotFoundException e) {
      return false;
    }
  }
}
