package com.brandon.restaurant_reservation_system.restaurants.exceptions;

public class BookingNotPossibleException extends RuntimeException {

	private static final long serialVersionUID = -2773926217313113756L;

	public BookingNotPossibleException(String message) {
		super(message);
	}

}