/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingDates;
import com.brandon.restaurant_reservation_system.tables.service.TableAllocatorService;
import com.brandon.restaurant_reservation_system.tables.service.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.tables.service.TableService;
import com.brandon.restaurant_reservation_system.users.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private UserService userService;
  @Mock
  private TableAllocatorService tableAllocatorService;
  @Mock
  private TableService tableService;
  @Mock
  private TableAvailabilityService tableAvailabilityService;
  @Mock
  private BookingDates bookingDates;
  @InjectMocks
  private BookingService bookingService;

  @Test
  void sortCombinationNameAndUpdateTable() {
    Mockito
        .when(tableService.find(Mockito.any(String.class)))
        .thenAnswer((Answer<RestaurantTable>) invocation -> {
          String tableName = invocation.getArgument(0);
          if (tableName.equals("20, 21")) {
            return new RestaurantTable("20, 21", 6, 2);
          }
          throw new TableNotFoundException(tableName);
        });
    Mockito
        .when(tableAvailabilityService
            .areTablesFree(Mockito.anyList(), Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
        .thenReturn(true);
    Mockito
        .when(bookingRepository.save(Mockito.any(Booking.class)))
        .thenReturn(CreateBookingsForTest.createBookingForTwoAt19());
    bookingService.updateTable(CreateBookingsForTest.createBookingForTwoAt19(), "21, 20");
  }
}