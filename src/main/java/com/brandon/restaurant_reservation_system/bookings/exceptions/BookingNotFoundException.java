package com.brandon.restaurant_reservation_system.bookings.exceptions;

public class BookingNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 8305734761535986611L;

  public BookingNotFoundException(long id) {
    super(String.format("Booking id not found: %d", id));
  }

  public BookingNotFoundException(String message) {
    super(message);
  }
}
