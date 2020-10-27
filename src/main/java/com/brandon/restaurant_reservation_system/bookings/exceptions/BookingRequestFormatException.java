/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.exceptions;

import com.brandon.restaurant_reservation_system.errors.ApiError;

public class BookingRequestFormatException extends RuntimeException {

    private static final long serialVersionUID = -8457339865555401532L;

    private ApiError apiError;

    public BookingRequestFormatException() {
        this("Improperly formatted request");
    }

    public BookingRequestFormatException(String message) {
        super(message);
    }

    public BookingRequestFormatException(ApiError apiError) {
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }
}
