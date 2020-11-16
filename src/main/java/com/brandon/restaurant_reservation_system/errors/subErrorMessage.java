/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.errors;

public class subErrorMessage extends ApiSubError {
    private final String object;
    private final String field;
    private final String rejectedValue;
    private final String message;

    public subErrorMessage(String object, String message) {
        this.object = object;
        this.message = message;
        this.field = null;
        this.rejectedValue = null;
    }

    @Override
    public String getObject() {
        return object;
    }

    @Override
    public void setObject(String object) {

    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public void setField(String field) {

    }

    @Override
    public Object getRejectedValue() {
        return null;
    }

    @Override
    public void setRejectedValue(Object rejectedValue) {

    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {

    }
}
