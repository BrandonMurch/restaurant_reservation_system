/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.UnallocatedBookingTableException;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TableHandlerServiceTest {

    @Mock
    private TableRepository tableRepository;
    @Mock
    private BookingHandlerService bookingHandler;
    @InjectMocks
    private TableHandlerService tableHandlerService;

    RestaurantTable table1 = new RestaurantTable("1", 4, 1);
    RestaurantTable table5 = new RestaurantTable("5", 4, 2);
    RestaurantTable table20 = new RestaurantTable("20", 2, 3);
    RestaurantTable table21 = new RestaurantTable("21", 2, 4);
    RestaurantTable table22 = new RestaurantTable("22", 2, 5);

    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();


    private final List<RestaurantTable> tableList = Arrays.asList(
      table1, table5, table20, table21, table22
    );

    private final List<CombinationOfTables> comboList = Arrays.asList(
      new CombinationOfTables(Arrays.asList(
        table20, table21
      ), 6),
      new CombinationOfTables(Arrays.asList(
        table20, table21, table22
      ), 7),
      new CombinationOfTables(Arrays.asList(
        table21, table22
      ), 8),
      new CombinationOfTables(Arrays.asList(
        table1, table5
      ), 9)
    );

    @Test
    void removeTableWhenNoExtraTables() {
        Mockito
          .when(tableRepository.findById(any(String.class)))
          .thenReturn(Optional.of(table1));
        Mockito
          .when(tableRepository.findAssociatedCombinations(Mockito.notNull()))
          .thenReturn(Collections.emptyList());
        Mockito
          .when(bookingHandler.freeTablesFromBookings(Mockito.notNull()))
          .thenReturn(Collections.singletonList(booking));

        var exception = assertThrows(UnallocatedBookingTableException.class,
          () -> tableHandlerService.remove(table1.getName()));

        String expected = "Bookings have been left without a table.";
        String actual = exception.getApiError().getMessage();

        assertEquals(expected, actual);
        assertEquals(1, exception.getApiError().getSubErrors().size());
    }

}