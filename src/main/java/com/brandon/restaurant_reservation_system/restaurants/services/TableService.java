/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.SubErrorMessage;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.DuplicateTableFoundException;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.UnallocatedBookingTableException;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TableService {

  @Autowired
  TableAllocatorService tableAllocator;
  @Autowired
  private TableRepository tableRepository;
  @Autowired
  private BookingService bookingService;

  public TableService() {
  }

  public RestaurantTable find(String tableNames) {
    Optional<RestaurantTable> optionalTable =
        tableRepository.findById(tableNames);
    if (optionalTable.isEmpty()) {
      throw new TableNotFoundException("Table is not found");
    }
    return optionalTable.get();
  }

  public List<RestaurantTable> findAll() {
    return tableRepository.findAll();
  }

  public void add(String name, int seats) {
    int length = getTableCount();
    add(new RestaurantTable(name, seats, length));
  }

  public void add(RestaurantTable table) {
    if (table.getPriority() == -1) {
      table.setPriority(getTableCount());
    }
    tableRepository.save(table);
  }

  public void addAll(List<RestaurantTable> restaurantTableList) {
    tableRepository.saveAll(restaurantTableList);
  }

  public void update(String name, RestaurantTable updated) {
    var result = tableRepository.findById(name);
    if (result.isEmpty()) {
      throw new TableNotFoundException(name);
    }
    RestaurantTable existing = result.get();
    existing.update(updated);
  }

  public void updateAll(List<RestaurantTable> tables) {
    tables.forEach(table -> this.update(table.getName(), table));
  }

  public void remove(String name) {
    Optional<RestaurantTable> result = tableRepository.findById(name);
    if (result.isPresent()) {
      RestaurantTable table = result.get();
      List<RestaurantTable> tablesAndCombinations = new ArrayList<>();
      tablesAndCombinations.add(table);
      tablesAndCombinations.addAll(tableRepository.findAssociatedCombinations(table));
      List<Booking> bookings =
          bookingService.freeTablesFromBookings(tablesAndCombinations);
      tableRepository.deleteWithAssociatedCombinations(table);
      if (!bookings.isEmpty()) {
        throw new UnallocatedBookingTableException(
            createApiErrorRemovingTables(bookings)
        );
      }
    } else {
      throw new TableNotFoundException(name);
    }
  }

  private ApiError createApiErrorRemovingTables(List<Booking> bookings) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT, "Bookings have " +
        "been left without a table.");
    bookings.forEach((booking) -> apiError.addSubError(new SubErrorMessage(booking.toString(),
        " was not able to be reassigned")));
    return apiError;
  }

  public RestaurantTable createCombination(List<RestaurantTable> tables) {
    int priority = getTableCount();
    RestaurantTable combination = new RestaurantTable(tables, priority);
    var result = tableRepository.findById(combination.getName());
    if (result.isPresent()) {
      throw new DuplicateTableFoundException(combination.getName());
    }
    return tableRepository.save(combination);
  }

  public RestaurantTable createCombination(String tableNames) {
    String[] splitTableNames = tableNames.split(", ");
    Arrays.sort(splitTableNames);
    List<RestaurantTable> tables = new ArrayList<>();
    for (String tableName : splitTableNames) {
      var result = tableRepository.findById(tableName);
      result.ifPresentOrElse(tables::add, () -> {
        throw new TableNotFoundException(tableName);
      });
    }
    return createCombination(tables);
  }

  public int getLargestTableSize() {
    return tableRepository.getLargestTableSize();
  }

  private int getTableCount() {
    return (int) tableRepository.count();
  }


}
