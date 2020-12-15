package com.brandon.restaurant_reservation_system.bookings.services;

import static com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService.validateBooking;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BookingValidationServiceTest {

  private final DateTimeFormatter timeFormat = GlobalVariables.getDateTimeFormat();

  @Test
  public void validateIncorrectPartySizeBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    booking.setPartySize(-1);

    Optional<ApiError> bookingValidationResponse =
        validateBooking(booking);
    assertTrue(bookingValidationResponse.isPresent());

    assertEquals(1,
        bookingValidationResponse.get().getSubErrors().size());

    String expectedMessage = "Party size must be greater than zero";

    assertTrue(bookingValidationResponse.get().getSubErrors().get(0).getMessage()
        .contains(expectedMessage));

  }

  @Test
  public void validateIncorrectDateBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    booking.setStartTime(null);
    booking.setEndTime(LocalDateTime.now().minusDays(2));
    Optional<ApiError> bookingValidationResponse =
        validateBooking(booking);
    assertTrue(bookingValidationResponse.isPresent());

    assertEquals(1,
        bookingValidationResponse.get().getSubErrors().size());
  }


  @Test
  public void validateCorrectBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();

    Optional<ApiError> bookingValidationResponse =
        validateBooking(booking);

    assertTrue(validateBooking(booking).isEmpty());
  }

}