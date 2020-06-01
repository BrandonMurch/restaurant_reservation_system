package com.brandon.restaurant_reservation_system.errors;

import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotFoundAdvice {

	@ResponseBody
	@ExceptionHandler({UserNotFoundException.class,
			BookingNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFoundHandler(RuntimeException ex) {
		return ex.getLocalizedMessage();
	}

}
