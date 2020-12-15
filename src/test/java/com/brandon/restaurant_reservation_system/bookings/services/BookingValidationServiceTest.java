package com.brandon.restaurant_reservation_system.bookings.services;

import static com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService.validateBooking;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class BookingValidationServiceTest {

  private final DateTimeFormatter timeFormat = GlobalVariables.getDateTimeFormat();

  @Test
  public void validateIncorrectPartySizeBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    booking.setPartySize(-1);
    String expectedMessage = "Party size must be greater than zero";

    try {
      validateBooking(booking);
      fail();
    } catch (BookingRequestFormatException exception) {
      int subErrorSize = exception.getApiError().getSubErrors().size();
      assertEquals(1, subErrorSize);
      assertTrue(exception.getApiError().getSubErrors().get(0).getMessage()
          .contains(expectedMessage));
    }
  }

  @Test
  public void validateIncorrectDateBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    booking.setStartTime(null);
    booking.setEndTime(LocalDateTime.now().minusDays(2));
    try {
      validateBooking(booking);
      fail();
    } catch (BookingRequestFormatException exception) {
      assertEquals(1,
          exception.getApiError().getSubErrors().size());
    }

  }


  @Test
  public void validateCorrectBookingTest() {
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();

    try {
      validateBooking(booking);
    } catch (BookingRequestFormatException exception) {
      fail();
    }
  }
}