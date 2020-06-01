package com.brandon.restaurant_reservation_system.bookings.exceptions;

public class BookingNotFoundException extends RuntimeException {

	public BookingNotFoundException(long id) {
		super(String.format("Booking id not found: %d", id));
	}

	public BookingNotFoundException(String message) {
		super(message);
	}
}
