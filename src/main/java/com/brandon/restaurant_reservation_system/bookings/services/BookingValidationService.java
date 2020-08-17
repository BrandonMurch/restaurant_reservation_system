package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BookingValidationService {

	private static void setDate(Booking booking) {
		if (booking.getDate() == null) {
			booking.setDate(booking.getStartTime().toLocalDate());
		}
	}

	public static Optional<ResponseEntity<ApiError>> validateBooking(Booking booking) {
		setDate(booking);
		boolean isStartTime = (booking.getStartTime() == null);
		boolean isPartySize =
		(booking.getPartySize() == null || booking.getPartySize() <= 0);

		if (isStartTime || isPartySize) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
			String exampleDateTime =
			LocalDateTime.now().format(
			DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (isStartTime) {
				apiError.addSubError(
				new ValidationError("Start Time", "Time must be " +
				"formatted " + exampleDateTime));
			}
			if (isPartySize) {
				ValidationError validationError = new ValidationError(
				"Party size",
				"Party size must be a positive integer");
				apiError.addSubError(validationError);

			}
			return Optional.of(new ResponseEntity<>(apiError,
			apiError.getStatus()));
		}
		return Optional.empty();
	}
}
