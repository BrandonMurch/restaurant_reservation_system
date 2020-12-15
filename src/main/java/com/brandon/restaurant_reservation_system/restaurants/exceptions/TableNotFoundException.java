/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.exceptions;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.RuntimeExceptionWithApIError;
import org.springframework.http.HttpStatus;

public class TableNotFoundException extends RuntimeExceptionWithApIError {

  private static final long serialVersionUID = 8305734761535986611L;
  private final ApiError apiError;

  public TableNotFoundException(String name) {
    super("Table " + name + " was not found");
    apiError = new ApiError(HttpStatus.NOT_FOUND, "Table " + name + " was not found");
  }

  public ApiError getApiError() {
    return apiError;
  }
}
