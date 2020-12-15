/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.exceptions;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.RuntimeExceptionWithApIError;
import org.springframework.http.HttpStatus;

public class DuplicateTableFoundException extends RuntimeExceptionWithApIError {

  private static final long serialVersionUID = 8305734761535986611L;
  private final ApiError apiError;

  public DuplicateTableFoundException(String name) {
    super("Table " + name + " exists already.");
    apiError = new ApiError(HttpStatus.CONFLICT, "Table " + name + " exists already.");
  }

  public ApiError getApiError() {
    return apiError;
  }
}
