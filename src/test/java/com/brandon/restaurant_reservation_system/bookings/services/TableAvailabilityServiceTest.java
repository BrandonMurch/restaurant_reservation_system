/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TableAvailabilityServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private TableAvailabilityService tableUpdater;

    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    Booking booking2 = CreateBookingsForTest.createBookingForFourAt20();
    RestaurantTable table = new RestaurantTable("K1", 2);
    RestaurantTable table2 = new RestaurantTable("20", 4);
    List<Booking> bookings;

    @BeforeEach
    public void setUp() {
        booking.addTable(table);
        booking2.addTable(table2);
        bookings = Arrays.asList(booking, booking2);
    }

    @Test
    void areTablesFree() {
        Mockito
          .when(bookingRepository.getBookingsDuringTime(any(LocalDateTime.class),
            any(LocalDateTime.class)))
          .thenReturn(bookings);

        Booking testBooking = CreateBookingsForTest.createBookingForTwoAt19();
        List<RestaurantTable> tables = Arrays.asList(table, table2);
        Boolean result = tableUpdater.areTablesFree(tables, testBooking.getStartTime(),
          testBooking.getEndTime());
        assertFalse(result);
    }

    @Test
    void isTableFree() {
        Mockito
          .when(bookingRepository.getBookingsDuringTime(any(LocalDateTime.class),
            any(LocalDateTime.class)))
          .thenReturn(bookings);

        Booking testBooking = CreateBookingsForTest.createBookingForTwoAt19();
        Boolean result = tableUpdater.isTableFree(table2, testBooking.getStartTime(),
          testBooking.getEndTime());
        assertFalse(result);
    }
}