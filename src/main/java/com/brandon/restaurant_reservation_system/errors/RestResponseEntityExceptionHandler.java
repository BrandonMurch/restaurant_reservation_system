package com.brandon.restaurant_reservation_system.errors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends
		ResponseEntityExceptionHandler {

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}


	@ExceptionHandler(value = {IllegalArgumentException.class,
			IllegalStateException.class})
	protected ResponseEntity<Object> HandleIllegalArgument(RuntimeException ex,
                                           WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
		return buildResponseEntity(apiError);
	}
}
