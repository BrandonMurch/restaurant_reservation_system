/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.exceptions;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.RuntimeExceptionWithApIError;

public class NoTableBookingsCreatedException extends RuntimeExceptionWithApIError {

    public NoTableBookingsCreatedException(ApiError apiError) {
        super(apiError);
    }

    private static final long serialVersionUID = 718701918334021620L;
}
