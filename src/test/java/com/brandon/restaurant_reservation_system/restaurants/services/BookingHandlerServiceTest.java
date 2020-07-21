/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.helpers.http.HttpRequestBuilder;
import com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingHandlerServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TableAllocatorService tableAllocatorService;
    @Mock
    private HttpRequestBuilder httpRequestBuilder;
    @InjectMocks
    private BookingHandlerService bookingHandler;

    private final CreateBookingsForTest createBooking = new CreateBookingsForTest();

    @Test
    void createBooking() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.singletonList(CreateTableForTest.getTable1()));
        Mockito
          .when(httpRequestBuilder.httpGetUsers(any(String.class)))
          .thenReturn(Collections.singletonList(user));
        Mockito
          .when(bookingRepository.getBookingsByUser(any(String.class)))
          .thenReturn(
            Collections
              .singletonList(createBooking.createBookingOnDifferentDate())
          );


        Optional<Booking> result = bookingHandler.createBooking(booking, user);

        assertEquals(Optional.of(booking), result);
    }

    @Test
    void createBookingNewUser() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.singletonList(CreateTableForTest.getTable1()));

        Optional<Booking> result = bookingHandler.createBooking(booking, user);

        assertEquals(Optional.of(booking), result);
    }

    @Test()
    void createBookingNotAvailable() {
        Booking booking = CreateBookingsForTest.createBookingForFourAt20();
        User user = booking.getUser();

        Mockito
          .when(tableAllocatorService.getAvailableTable(any(Booking.class)))
          .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(BookingNotPossibleException.class, () -> {
            Optional<Booking> result = bookingHandler.createBooking(booking, user);
        });

        String expectedMessage = "Requested date is not available";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}