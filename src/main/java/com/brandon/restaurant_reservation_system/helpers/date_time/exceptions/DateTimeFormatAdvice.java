package com.brandon.restaurant_reservation_system.helpers.date_time.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DateTimeFormatAdvice {

	@ResponseBody
	@ExceptionHandler(DateTimeFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String DateFormatExceptionHandler(DateTimeFormatException ex) {
		return ex.getLocalizedMessage();
	}
}
