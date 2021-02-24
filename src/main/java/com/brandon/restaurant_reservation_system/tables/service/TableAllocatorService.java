/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.tables.service;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class TableAllocatorService {

  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private TableService tableService;
  @Autowired
  private RestaurantConfig config;
  @Autowired
  private HoursOfOperation hoursOfOperation;
  private Map<Integer, RestaurantTable> availableTables;

  public TableAllocatorService() {
  }

  public Optional<RestaurantTable> getAvailableTable(Booking booking) {
    if (booking.getEndTime() == null) {
      booking.setEndTime(
          booking.getStartTime()
              .plus(config.getStandardBookingDuration())
      );
    }
    return getAvailableTable(
        booking.getStartTime(),
        booking.getEndTime(),
        booking.getPartySize());
  }


  public Optional<RestaurantTable> getAvailableTable(LocalDateTime startTime,
      int partySize) {
    LocalDateTime endTime =
        startTime.plus(config.getStandardBookingDuration());
    return getAvailableTable(startTime, endTime, partySize);
  }

  public Optional<RestaurantTable> getAvailableTable(LocalDateTime startTime,
      LocalDateTime endTime,
      int partySize) {
    List<RestaurantTable> allTables = tableService.findAll();
    if (allTables == null || allTables.isEmpty()) {
      throw new IllegalStateException("Please ensure the restaurant is " +
          "set up with tables before trying to make a booking.");
    } else if (!hoursOfOperation.isBookingTime(startTime)) {
      return Optional.empty();
    }

    List<Booking> bookings = getBookings(startTime, endTime);

    Optional<Map<RestaurantTable, Booking>> optionalResults =
        getOccupiedTables(bookings);

    if (optionalResults.isEmpty()) {
      return Optional.empty();
    }

    Map<RestaurantTable, Booking> occupiedTables = optionalResults.get();
    var optionalTable = getTableBySizeAndUpdateMap(occupiedTables,
        partySize);
    if (optionalTable.isEmpty()) {
      if (config.canABookingOccupyALargerTable() || partySize % 2 == 1) {
        return getATableRecursively(partySize + 1);
      }
    }
    return optionalTable;
  }

  private Optional<RestaurantTable> getATableRecursively(int partySize) {
    if (availableTables.containsKey(partySize)) {
      return Optional.of(availableTables.get(partySize));
    }
    if (partySize >= tableService.getLargestTableSize()) {
      return Optional.empty();
    }
    return getATableRecursively(partySize + 1);
  }

  private List<Booking> getBookings(LocalDateTime startTime,
      LocalDateTime endTime) {
    List<Booking> bookings =
        bookingRepository.getBookingsDuringTime(startTime,
            endTime);
    if (bookings == null) {
      throw new IllegalStateException("Connection to the booking " +
          "database failed.");
    }

    return bookings;
  }


  protected Optional<Map<RestaurantTable, Booking>> getOccupiedTables(
      List<Booking> bookings) {
    Map<RestaurantTable, Booking> occupiedTables = new HashMap<>();

    int capacityCount = 0;
    for (Booking booking : bookings) {

      // Check capacity. Return an empty list if capacity is reached.
      capacityCount += booking.getPartySize();
      if (capacityCount > config.getCapacity()) {
        return Optional.empty();
      }

      booking.getTables()
          .forEach(table -> occupiedTables.put(table, booking));
    }
    return Optional.of(occupiedTables);
  }

  protected Optional<RestaurantTable> getTableBySizeAndUpdateMap(
      Map<RestaurantTable, Booking> occupiedTables,
      int size) {
    availableTables = new HashMap<>();
    for (RestaurantTable table : tableService.findAll()) {
      boolean foundAOccupiedTable = false;
      for (RestaurantTable restaurantTable : table.getTables()) {
        if (occupiedTables.containsKey(restaurantTable)) {
          foundAOccupiedTable = true;
          break;
        }
      }

      if (!foundAOccupiedTable) {
        if (table.getSeats() == size) {
          return Optional.of(table);
        } else if (!availableTables.containsKey(table.getSeats())) {
          availableTables.put(table.getSeats(),
              table);
        }
      }
    }
    return Optional.empty();
  }

  protected Map<Integer, RestaurantTable> getAvailableTablesForTest() {
    return availableTables;
  }

}

