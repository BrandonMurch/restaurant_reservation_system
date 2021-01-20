/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDate;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingDates;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingTimes;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

  private final DateTimeFormatter dateFormat =
      GlobalVariables.getDateFormat();
  @Autowired
  private BookingDates bookingDates;
  @Autowired
  private BookingTimes bookingTimes;

  public RestaurantController() {
  }

  @GetMapping(value = "/availability")
  public ResponseEntity<?> getAvailableBookingTimes(
      @RequestParam(required = false) String date,
      @RequestParam(required = false) Integer size) {

    if (date != null && size != null) {
      LocalDate parsedDate = parseDate(date, dateFormat);
      Set<LocalTime> set = bookingTimes.getAvailableBySize(size, parsedDate);
      return new ResponseEntity<>(set, HttpStatus.OK);
    } else {
      return ResponseEntity.ok(bookingDates.getAll());
    }
  }



  // admin only controller options - to be implemented in future
  // GET /RESTAURANTS/{RESTAURANT} - get a restaurant
  // POST /RESTAURANTS/ - create a restaurant
  // PUT /RESTAURANTS/{RESTAURANT} - update a restaurant

}
