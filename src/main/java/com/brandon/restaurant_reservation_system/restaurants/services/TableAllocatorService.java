/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
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
  private Restaurant restaurant;
  private Map<Integer, RestaurantTable> availableTables;

  public TableAllocatorService() {
  }

  public Optional<RestaurantTable> getAvailableTable(Booking booking) {
    if (booking.getEndTime() == null) {
      booking.setEndTime(
          booking.getStartTime()
              .plus(restaurant.getStandardBookingDuration())
      );
    }
    return getAvailableTable(
        booking.getStartTime(),
        booking.getEndTime(),
        booking.getPartySize(),
        restaurant.canABookingOccupyALargerTable());
  }


  public Optional<RestaurantTable> getAvailableTable(LocalDateTime startTime,
      int partySize,
      boolean searchGreaterSizes) {
    LocalDateTime endTime =
        startTime.plus(restaurant.getStandardBookingDuration());
    return getAvailableTable(startTime, endTime, partySize,
        searchGreaterSizes);
  }

  public Optional<RestaurantTable> getAvailableTable(LocalDateTime startTime,
      LocalDateTime endTime,
      int partySize,
      boolean searchGreaterSizes) {
    List<RestaurantTable> allTables = tableService.findAll();
    if (allTables == null || allTables.isEmpty()) {
      throw new IllegalStateException("Please ensure the restaurant is " +
          "set up with tables before trying to make a booking.");
    } else if (!restaurant.isBookingTime(startTime)) {
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
      if (searchGreaterSizes || partySize % 2 == 1) {
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

  private List<Booking> getBookings(LocalDateTime startTime) {
    return getBookings(startTime,
        startTime.plus(restaurant.getStandardBookingDuration()));
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
      if (capacityCount > restaurant.getCapacity()) {
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

  public SortedSet<LocalTime> getAvailableTimes(int size, LocalDate date) {
    List<LocalTime> times = restaurant.getBookingTimes(date);
    SortedSet<LocalTime> availableTimes = new TreeSet<>();

    for (LocalTime time : times) {
      if (date.isEqual(LocalDate.now()) && time.isBefore(
          LocalTime.now())) {
        continue;
      }
      LocalDateTime dateTime = date.atTime(time);

      if (getAvailableTable(dateTime, size,
          restaurant.canABookingOccupyALargerTable()).isPresent()) {
        availableTimes.add(time);
      }
    }
    return availableTimes;
  }

  protected Map<Integer, RestaurantTable> getAvailableTablesForTest() {
    return availableTables;
  }

}

