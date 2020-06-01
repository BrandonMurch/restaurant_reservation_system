package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.helpers.json.JsonConverter;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService.validateBooking;
import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BookingValidationServiceTest {
	private DateTimeFormatter timeFormat = GlobalVariables.getDateTimeFormat();

	@Test
	public void validateBookingTest() throws JsonProcessingException {
		Booking correctBooking = new Booking(4,
				parseDateTime("2020-10-11T19:00:00.00", timeFormat),
				parseDateTime("2020-10-11T23:00:00.00", timeFormat),
				new User());

		String incorrectBookingJson = "{\"id\":0,\"partySize\":-1," +
				"\"startTime\": \"2020d-10-11T19:00:00\", " +
				"\"endTime\": \"20s20-10-11T23:00:00\"}";
		Booking incorrectBooking = JsonConverter
				.jsonToObject(incorrectBookingJson, Booking.class);

		assertTrue(validateBooking(correctBooking).isEmpty());

		Optional<ResponseEntity<ApiError>> bookingValidationResponse =
				validateBooking(incorrectBooking);
		assertTrue(bookingValidationResponse.isPresent());

		assertEquals(3,
				bookingValidationResponse.get()
						.getBody().getSubErrors().size());
	}

}