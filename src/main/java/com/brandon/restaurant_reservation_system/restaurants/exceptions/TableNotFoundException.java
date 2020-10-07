/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.exceptions;

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(long id) {
        super(String.format("Booking id not found: %d", id));
    }

    public TableNotFoundException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 8305734761535986611L;
}
