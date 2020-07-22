/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.exceptions;

public class DuplicateFoundException extends RuntimeException {
    private static final long serialVersionUID = -5971113769133497554L;

    public DuplicateFoundException() {
        this("Request not possible, duplicate already found");
    }

    public DuplicateFoundException(String message) {
        super(message);
    }

}
