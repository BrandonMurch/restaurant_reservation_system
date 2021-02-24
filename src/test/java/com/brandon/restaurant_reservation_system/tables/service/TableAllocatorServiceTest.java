/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.tables.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableAllocatorServiceTest {

  private final LocalDateTime dateTime1 = LocalDateTime.now();
  private final LocalDateTime dateTime2 = LocalDateTime.now().plusHours(2);
  private final List<RestaurantTable> tableList = Arrays.asList(
      new RestaurantTable("k1", 2, 1),
      new RestaurantTable("k2", 2, 2),
      new RestaurantTable("b1", 2, 3),
      new RestaurantTable("b2", 2, 3),
      new RestaurantTable("1", 4, 3),
      new RestaurantTable("5", 4, 3),
      new RestaurantTable("20", 2, 3),
      new RestaurantTable("21", 2, 3),
      new RestaurantTable("22", 2, 3),
      new RestaurantTable("23", 2, 3),
      new RestaurantTable("24", 2, 3),
      new RestaurantTable("25", 2, 3)
  );
  @Mock
  private HoursOfOperation bookingTimes;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private TableService tableService;
  @Mock
  private RestaurantConfig config;
  @InjectMocks
  private TableAllocatorService tableAllocator;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getAvailableTableWithBooking() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(true);
    Mockito
        .when(bookingRepository
            .getBookingsDuringTime(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        )
        .thenReturn(Collections.emptyList());

    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();

    tableAllocator.getAvailableTable(booking)
        .ifPresentOrElse((table) -> assertEquals(tableList.get(0), table),
            () -> fail("No available table was found"));
  }

  @Test
  void testGetAvailableTableWithOnlyStartTime() {
    Mockito
        .when(config.getStandardBookingDuration())
        .thenReturn(Duration.ofHours(2));
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(true);
    Mockito
        .when(bookingRepository
            .getBookingsDuringTime(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        )
        .thenReturn(Collections.emptyList());

    tableAllocator.getAvailableTable(dateTime1, 2)
        .ifPresentOrElse((table) -> assertEquals(tableList.get(0), table),
            () -> fail("No available table was found"));

  }

  @Test
  void getAvailableTableBookingTimeNotValid() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(false);

    Optional<RestaurantTable> result = tableAllocator.getAvailableTable(dateTime1,
        dateTime2, 2);

    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void getAvailableTableNoTables() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(Collections.emptyList());
    Exception exception = Assertions.assertThrows(
        IllegalStateException.class,
        () -> tableAllocator.getAvailableTable(dateTime1,
            dateTime2, 2));

    String expectedMessage = "Please ensure the restaurant is " +
        "set up with tables before trying to make a booking.";

    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void getAvailableTable() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(true);
    Mockito
        .when(bookingRepository
            .getBookingsDuringTime(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        )
        .thenReturn(Collections.emptyList());

//    Optional<RestaurantTable> result =
    tableAllocator
        .getAvailableTable(dateTime1, dateTime2, 2)
        .ifPresentOrElse(
            (table) -> assertEquals(tableList.get(0), table),
            () -> fail("A table was not returned")
        );
  }

  @Test
  void getAvailableTableGreaterSize() {
    RestaurantTable table = new RestaurantTable("30", 4, 1);

    Mockito
        .when(config.canABookingOccupyALargerTable())
        .thenReturn(true);
    Mockito
        .when(tableService.findAll())
        .thenReturn(Collections.singletonList(table));
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(true);
    Mockito
        .when(bookingRepository
            .getBookingsDuringTime(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        )
        .thenReturn(Collections.emptyList());
    Mockito
        .when(tableService.getLargestTableSize())
        .thenReturn(6);

    tableAllocator.getAvailableTable(dateTime1, dateTime2, 2)
        .ifPresentOrElse((result) -> assertEquals(table, result),
            () -> fail("No available table was found"));
  }

  @Test
  void getOccupiedTables() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    booking.setTables(tableList.get(0));
    List<Booking> bookings = Collections.singletonList(booking);

    Mockito
        .when(config.getCapacity())
        .thenReturn(20000);

    Optional<Map<RestaurantTable, Booking>> optionalResults =
        tableAllocator.getOccupiedTables(bookings);

    if (optionalResults.isEmpty()) {
      fail();
    }

    Map<RestaurantTable, Booking> map = optionalResults.get();

    assertEquals(1, map.keySet().size());
    Assertions.assertTrue(map.containsKey(tableList.get(0)));
    Assertions.assertTrue(map.containsValue(booking));
  }


  @Test
  void getOccupiedTablesOverCapacity() {
    List<Booking> bookings =
        Collections.singletonList(CreateBookingsForTest.createBookingForTwoAt19());
    Mockito
        .when(config.getCapacity())
        .thenReturn(0);

    assertEquals(Optional.empty(), tableAllocator.getOccupiedTables(bookings));
  }

  @Test
  void getTableBySize() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    var results =
        tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 2);
    results.ifPresentOrElse(
        (result) -> assertEquals(tableList.get(0), result),
        () -> fail("No table was found")
    );
  }

  @Test
  void updateTableSizeMap() {
    List<RestaurantTable> oneTableList =
        Collections.singletonList(new RestaurantTable("30", 4, 1));

    Mockito
        .when(tableService.findAll())
        .thenReturn(oneTableList);
    Optional<RestaurantTable> results =
        tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 2);
    assertEquals(Optional.empty(), results);
    Assertions.assertFalse(tableAllocator.getAvailableTablesForTest().isEmpty());
    Assertions.assertTrue(
        tableAllocator.getAvailableTablesForTest().containsKey(oneTableList.get(0).getSeats()));
  }

  @Test
  void restaurantOverCapacity() {
    Mockito
        .when(tableService.findAll())
        .thenReturn(tableList);
    Mockito
        .when(bookingTimes.isBookingTime(any(LocalDateTime.class)))
        .thenReturn(true);
    List<Booking> bookings =
        Collections.singletonList(CreateBookingsForTest.createBookingForTwoAt19());
    Mockito
        .when(bookingRepository
            .getBookingsDuringTime(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        )
        .thenReturn(bookings);

    tableAllocator.getAvailableTable(dateTime1, dateTime2, 2)
        .ifPresent((table) -> fail());
  }

}