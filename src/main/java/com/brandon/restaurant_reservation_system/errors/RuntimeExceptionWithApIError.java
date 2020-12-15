/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.errors;

import org.springframework.http.HttpStatus;

public abstract class RuntimeExceptionWithApIError extends RuntimeException {

  private static final long serialVersionUID = -6983993997921437390L;
  private final ApiError apiError;

  public RuntimeExceptionWithApIError(String message) {
    super(message);
    this.apiError = new ApiError(HttpStatus.BAD_REQUEST, message);
  }

  public RuntimeExceptionWithApIError(String message, HttpStatus status) {
    super(message);
    apiError = new ApiError(status, message);
  }

  public RuntimeExceptionWithApIError(ApiError apiError) {
    this.apiError = apiError;
  }

  public ApiError getApiError() {
    return apiError;
  }
}
