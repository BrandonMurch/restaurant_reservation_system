/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.tables.service;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TableAvailabilityService {

  @Autowired
  private BookingRepository bookingRepository;

  public Boolean areTablesFree(List<? extends RestaurantTable> tables, LocalDateTime start,
      LocalDateTime end) {
    for (RestaurantTable table : tables) {
      if (!isTableFree(table, start, end)) {
        return false;
      }
    }
    return true;
  }

  public Boolean isTableFree(RestaurantTable table, LocalDateTime start,
      LocalDateTime end) {
    List<Booking> bookings = bookingRepository.getBookingsDuringTime(start, end);
    for (Booking booking : bookings) {
      if (booking.getTables().contains(table)) {
        return false;
      }
    }
    return true;
  }
}
