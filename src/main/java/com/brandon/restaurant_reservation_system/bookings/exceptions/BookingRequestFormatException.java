/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.exceptions;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.RuntimeExceptionWithApIError;

public class BookingRequestFormatException extends RuntimeExceptionWithApIError {

    private static final long serialVersionUID = -8457339865555401532L;


    public BookingRequestFormatException() {
        this("Improperly formatted request");
    }

    public BookingRequestFormatException(String message) {
        super(message);
    }

    public BookingRequestFormatException(ApiError apiError) {
        super(apiError);
    }
}
