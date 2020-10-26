/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.exceptions;

public class BookingNotPossibleException extends RuntimeException {

    private static final long serialVersionUID = -2773926217313113756L;

    private final boolean forcible;

    public BookingNotPossibleException(String message) {
        super(message);
        this.forcible = false;
    }

    public BookingNotPossibleException(String message, boolean forcible) {
        super(message);
        this.forcible = forcible;
    }

    public boolean isForcible() {
        return forcible;
    }
}