/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.exceptions;

public class BookingRequestFormatException extends RuntimeException {

    private static final long serialVersionUID = -8457339865555401532L;

    public BookingRequestFormatException() {
        this("Improperly formatted request");
    }

    public BookingRequestFormatException(String message) {
        super(message);
    }
}
