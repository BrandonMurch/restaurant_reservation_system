/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TableAllocatorServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private Restaurant restaurant;
    @Mock
    private BookingHandlerService bookingHandlerService;
    @InjectMocks
    private TableAllocatorService tableAllocator;
    private final LocalDateTime dateTime1 = LocalDateTime.now();
    private final LocalDateTime dateTime2 = LocalDateTime.now().plusHours(2);
    private final CreateBookingsForTest createBookingsForTest = new CreateBookingsForTest();

    private final List<RestaurantTable> tableList = Arrays.asList(
      new RestaurantTable("k1", 2),
      new RestaurantTable("k2", 2),
      new RestaurantTable("b1", 2),
      new RestaurantTable("b2", 2),
      new RestaurantTable("1", 4),
      new RestaurantTable("5", 4),
      new RestaurantTable("20", 2),
      new RestaurantTable("21", 2),
      new RestaurantTable("22", 2),
      new RestaurantTable("23", 2),
      new RestaurantTable("24", 2),
      new RestaurantTable("25", 2)
    );

    private final List<CombinationOfTables> comboList = Arrays.asList(
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("21", 2),
        new RestaurantTable("22", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("21", 2),
        new RestaurantTable("22", 2),
        new RestaurantTable("23", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("22", 2),
        new RestaurantTable("23", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("1", 4),
        new RestaurantTable("5", 4)
      ))
    );

    @Test
    void getAvailableTableWithBooking() {
        Mockito
          .when(restaurant.getTableList())
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
        Booking booking = createBookingsForTest.createBookingForTwoAt19();

        assertEquals(Collections.singletonList(tableList.get(0)),
          tableAllocator.getAvailableTable(booking));
    }

    @Test
    void testGetAvailableTableWithOnlyStartTime() {
        Mockito
          .when(restaurant.getStandardBookingDuration())
          .thenReturn(Duration.ofHours(2));
        Mockito
          .when(restaurant.getTableList())
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
          .when(restaurant.getTableList())
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
          .when(restaurant.getTableList())
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
          .when(restaurant.getTableList())
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
          Collections.singletonList(new RestaurantTable("30", 4));

        Mockito
          .when(restaurant.getTableList())
          .thenReturn(oneTableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.hasCombinationsOfTables())
          .thenReturn(false);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());
        Mockito
          .when(restaurant.getLargestTableSize())
          .thenReturn(6);

        assertEquals(oneTableList, tableAllocator.getAvailableTable(dateTime1,
          dateTime2, 2
          , true));
    }

    @Test
    void getAvailableTableWithCombinations() {
        Mockito
          .when(restaurant.getTableList())
          .thenReturn(Collections.singletonList(
            new RestaurantTable("200", 10)
          ));
        Mockito
          .when(restaurant.getCombinationsOfTables())
          .thenReturn(comboList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.hasCombinationsOfTables())
          .thenReturn(true);
        Mockito
          .when(bookingRepository
            .getBookingsDuringTime(
              any(LocalDateTime.class),
              any(LocalDateTime.class))
          )
          .thenReturn(Collections.emptyList());


        assertEquals(comboList.get(0).getRestaurantTables(), tableAllocator.getAvailableTable(dateTime1,
          dateTime2, 4
          , false));
    }

    @Test
    void getOccupiedTables() {
        Booking booking = createBookingsForTest.createBookingForTwoAt19();
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
          Collections.singletonList(createBookingsForTest.createBookingForTwoAt19());
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
          Collections.singletonList(new RestaurantTable("30", 4));
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
          .when(restaurant.getTableList())
          .thenReturn(tableList);
        Mockito
          .when(restaurant.isBookingTime(any(LocalDateTime.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.getCapacity())
          .thenReturn(0);
        List<Booking> bookings =
          Collections.singletonList(createBookingsForTest.createBookingForTwoAt19());
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
          .when(restaurant.getCombinationsOfTables())
          .thenReturn(comboList);
        List<RestaurantTable> results =
          tableAllocator.getCombinationBySizeAndUpdateMap(new HashMap<>(), 4);
        assertEquals(comboList.get(0).getRestaurantTables(), results);
    }

    @Test
    void updateCombinationsMap() {
        Mockito
          .when(restaurant.getCombinationsOfTables())
          .thenReturn(Collections.singletonList(comboList.get(0)));
        List<RestaurantTable> results =
          tableAllocator.getCombinationBySizeAndUpdateMap(new HashMap<>(), 50);
        assertEquals(Collections.emptyList(), results);
        assertFalse(tableAllocator.getAvailableCombinationsForTest().isEmpty());
        assertTrue(tableAllocator.getAvailableCombinationsForTest().containsKey(comboList.get(0).getTotalSeats()));
    }

    @Test
    void getAvailableTimes() {
        Mockito
          .when(restaurant.getStandardBookingDuration())
          .thenReturn(Duration.ofHours(2));
        Mockito
          .when(restaurant.getTableList())
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
          LocalDate.now()));
    }

    @Test
    void getAvailableDates() {
        Mockito
          .when(restaurant.getBookingDateRange())
          .thenReturn(new DateRange(LocalDate.now(), LocalDate.now().plusDays(2)));
        Mockito
          .when(restaurant.isOpenOnDate(any(LocalDate.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.getStandardBookingDuration())
          .thenReturn(Duration.ofHours(2));
        Mockito
          .when(restaurant.getTableList())
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

        SortedSet<LocalDate> expected = new TreeSet<>();
        for (int i = 0; i < 3; i++) {
            expected.add(LocalDate.now().plusDays(i));
        }
        assertEquals(expected, tableAllocator.getAvailableDates());
    }
}