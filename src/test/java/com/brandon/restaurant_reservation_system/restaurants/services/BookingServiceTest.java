/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingService;
import com.brandon.restaurant_reservation_system.helpers.http.HttpRequestBuilder;
import com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TableAllocatorService tableAllocatorService;
    @Mock
    private HttpRequestBuilder httpRequestBuilder;
    @InjectMocks
    private BookingService bookingHandler;

    private final CreateBookingsForTest createBooking = new CreateBookingsForTest();

    @Test
    void createBooking() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.singletonList(CreateTableForTest.getTable1()));


        Booking result = bookingHandler.createBooking(booking, user, false);

        assertEquals(booking, result);
    }

    @Test
    void createBookingNewUser() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.singletonList(CreateTableForTest.getTable1()));

        Booking result = bookingHandler.createBooking(booking, user, false);

        assertEquals(booking, result);
    }

    @Test()
    void createBookingNotAvailable() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(BookingNotPossibleException.class, () -> {
            Booking result = bookingHandler.createBooking(booking, user, false);
        });

        String expectedMessage = "Requested date is not available";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}