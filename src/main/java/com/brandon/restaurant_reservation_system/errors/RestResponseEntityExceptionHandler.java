/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.errors;

import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.DuplicateFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends
ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {DuplicateFoundException.class})
	@ResponseStatus(value = HttpStatus.CONFLICT)
	protected ResponseEntity<Object> HandleConflict(RuntimeException ex,
													WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex);
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	@ExceptionHandler(value = {IllegalArgumentException.class,
	IllegalStateException.class,
	BookingRequestFormatException.class,
	BookingNotPossibleException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> HandleBadRequest(RuntimeException ex,
													  WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
