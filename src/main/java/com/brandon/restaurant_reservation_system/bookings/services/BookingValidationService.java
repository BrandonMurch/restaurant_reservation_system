package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BookingValidationService {

	public static Optional<ResponseEntity<ApiError>> validateBooking(Booking booking) {
		boolean startTime = (booking.getStartTime() ==  null);
		boolean endTime = (booking.getEndTime() == null);
		boolean partySize =
				(booking.getPartySize() == null || booking.getPartySize() <= 0);


		if (startTime || endTime || partySize) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
			String exampleDateTime =
					LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (startTime){
				apiError.addSubError(
						new ValidationError("Start Time", "Time must be " +
								"formatted " + exampleDateTime));
			}
			if (endTime) {
				apiError.addSubError(new ValidationError("End Time", "Time " +
						"must be " +
						"formatted " + exampleDateTime));
			}
			if (partySize) {
				ValidationError validationError = new ValidationError("Party size",
						"Party size must be a positive integer");
				apiError.addSubError(validationError);

			}
			return Optional.of(new ResponseEntity(apiError,
					apiError.getStatus()));
		}
		return Optional.empty();
	}
}
