/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.model.SingleTable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TableAllocatorServiceTest {

    @Mock
    private Restaurant restaurant;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TableService tableService;
    @InjectMocks
    private TableAllocatorService tableAllocator;
    private final LocalDateTime dateTime1 = LocalDateTime.now();
    private final LocalDateTime dateTime2 = LocalDateTime.now().plusHours(2);

    private final List<RestaurantTable> tableList = Arrays.asList(
        new SingleTable("k1", 2, 1),
        new SingleTable("k2", 2, 2),
        new SingleTable("b1", 2, 3),
        new SingleTable("b2", 2, 3),
        new SingleTable("1", 4, 3),
        new SingleTable("5", 4, 3),
        new SingleTable("20", 2, 3),
        new SingleTable("21", 2, 3),
        new SingleTable("22", 2, 3),
        new SingleTable("23", 2, 3),
        new SingleTable("24", 2, 3),
        new SingleTable("25", 2, 3)
    );

    private final List<CombinationOfTables> comboList = Arrays.asList(
      new CombinationOfTables(Arrays.asList(
          new SingleTable("21", 2, 1),
          new SingleTable("22", 2, 1)
      ), 1),
      new CombinationOfTables(Arrays.asList(
          new SingleTable("21", 2, 1),
          new SingleTable("22", 2, 1),
          new SingleTable("23", 2, 1)
      ), 1),
      new CombinationOfTables(Arrays.asList(
          new SingleTable("22", 2, 1),
          new SingleTable("23", 2, 1)
      ), 1),
      new CombinationOfTables(Arrays.asList(
          new SingleTable("1", 4, 1),
          new SingleTable("5", 4, 1)
      ), 1)
    );

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
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());
        Booking booking = CreateBookingsForTest.createBookingForTwoAt19();

        assertEquals(Collections.singletonList(tableList.get(0)),
          tableAllocator.getAvailableTable(booking));
    }

    @Test
    void testGetAvailableTableWithOnlyStartTime() {
        Mockito
          .when(restaurant.getStandardBookingDuration())
          .thenReturn(Duration.ofHours(2));
        Mockito
            .when(tableService.findAll())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());

        assertEquals(Collections.singletonList(tableList.get(0)),
          tableAllocator.getAvailableTable(dateTime1, 2
            , false));
    }

    @Test
    void getAvailableTableBookingTimeNotValid() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(false);
        assertEquals(Collections.emptyList(), tableAllocator.getAvailableTable(dateTime1,
          dateTime2, 2
          , false));
    }

    @Test
    void getAvailableTableNoTables() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(Collections.emptyList());
        Exception exception = assertThrows(
          IllegalStateException.class,
          () -> tableAllocator.getAvailableTable(dateTime1,
            dateTime2, 2
            , false));

        String expectedMessage = "Please ensure the restaurant is " +
          "set up with tables before trying to make a booking.";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getAvailableTable() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());

        assertEquals(Collections.singletonList(tableList.get(0)),
          tableAllocator.getAvailableTable(dateTime1,
            dateTime2, 2
            , false));
    }

    @Test
    void getAvailableTableGreaterSize() {
        List<RestaurantTable> oneTableList =
            Collections.singletonList(new SingleTable("30", 4, 1));

        Mockito
            .when(tableService.findAll())
          .thenReturn(oneTableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
            .when(tableService.doCombinationsExist())
          .thenReturn(false);
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

        assertEquals(oneTableList, tableAllocator.getAvailableTable(dateTime1,
          dateTime2, 2
          , true));
    }

    @Test
    void getAvailableTableWithCombinations() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(Collections.singletonList(
              new SingleTable("200", 10, 1)
          ));
        Mockito
            .when(tableService.getAllCombinations())
          .thenReturn(comboList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
            .when(tableService.doCombinationsExist())
          .thenReturn(true);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());

        assertEquals(comboList.get(0).getAssociatedTables(),
            tableAllocator.getAvailableTable(dateTime1,
                dateTime2, 4
                , false));
    }

    @Test
    void getOccupiedTables() {
        Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
        booking.setTables(Collections.singletonList(tableList.get(0)));
        List<Booking> bookings = Collections.singletonList(booking);

        Mockito
          .when(restaurant.getCapacity())
          .thenReturn(20000);

        Optional<Map<RestaurantTable, Booking>> optionalResults =
          tableAllocator.getOccupiedTables(bookings);

        if (optionalResults.isEmpty()) {
            fail();
        }

        Map<RestaurantTable, Booking> map = optionalResults.get();

        assertEquals(1, map.keySet().size());
        assertTrue(map.containsKey(tableList.get(0)));
        assertTrue(map.containsValue(booking));
    }


    @Test
    void getOccupiedTablesOverCapacity() {
        List<Booking> bookings =
          Collections.singletonList(CreateBookingsForTest.createBookingForTwoAt19());
        Mockito
          .when(restaurant.getCapacity())
          .thenReturn(0);

        assertEquals(Optional.empty(), tableAllocator.getOccupiedTables(bookings));
    }

    @Test
    void getTableBySize() {
        ReflectionTestUtils.setField(tableAllocator, "restaurantTableList",
          this.tableList);
        List<RestaurantTable> results =
          tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 2);
        assertEquals(Collections.singletonList(tableList.get(0)), results);
    }

    @Test
    void updateTableSizeMap() {
        List<RestaurantTable> oneTableList =
            Collections.singletonList(new SingleTable("30", 4, 1));
        ReflectionTestUtils.setField(tableAllocator, "restaurantTableList",
          oneTableList);
        List<RestaurantTable> results =
          tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 2);
        assertEquals(Collections.emptyList(), results);
        assertFalse(tableAllocator.getAvailableTablesForTest().isEmpty());
        assertTrue(tableAllocator.getAvailableTablesForTest().containsKey(oneTableList.get(0).getSeats()));
    }

    @Test
    void restaurantOverCapacity() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.getCapacity())
          .thenReturn(0);
        List<Booking> bookings =
          Collections.singletonList(CreateBookingsForTest.createBookingForTwoAt19());
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(bookings);

        assertEquals(Collections.emptyList(),
          tableAllocator.getAvailableTable(dateTime1,
            dateTime2, 2
            , false));
    }


    @Test
    void getCombinationBySize() {
        Mockito
            .when(tableService.getAllCombinations())
            .thenReturn(comboList);
        List<RestaurantTable> results =
            tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 4);
        assertEquals(comboList.get(0).getAssociatedTables(), results);
    }

    @Test
    void updateCombinationsMap() {
        Mockito
            .when(tableService.findAll())
          .thenReturn(Collections.singletonList(comboList.get(0)));
        List<RestaurantTable> results =
            tableAllocator.getTableBySizeAndUpdateMap(new HashMap<>(), 50);
        assertEquals(Collections.emptyList(), results);
        assertFalse(tableAllocator.getAvailableCombinationsForTest().isEmpty());
        assertTrue(tableAllocator.getAvailableCombinationsForTest().containsKey(comboList.get(0).getSeats()));
    }

    @Test
    void getAvailableTimes() {
        Mockito
          .when(restaurant.getStandardBookingDuration())
          .thenReturn(Duration.ofHours(2));
        Mockito
            .when(tableService.findAll())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        List<LocalTime> bookingTimes = Arrays.asList(
          LocalTime.of(18, 0),
          LocalTime.of(20, 0)
        );
        Mockito
          .when(restaurant.getBookingTimes(any(LocalDate.class)))
          .thenReturn(bookingTimes);
        Mockito
          .when(restaurant.canABookingOccupyALargerTable())
          .thenReturn(false);
        assertEquals(new TreeSet<>(bookingTimes), tableAllocator.getAvailableTimes(2,
          LocalDate.now().plusDays(1)));
    }


}