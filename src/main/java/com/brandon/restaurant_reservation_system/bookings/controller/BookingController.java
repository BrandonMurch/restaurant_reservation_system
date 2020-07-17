/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final DateTimeFormatter dateFormat = GlobalVariables.getDateFormat();
	private final DateTimeFormatter dateTimeFormat =
	GlobalVariables.getDateTimeFormat();
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private BookingHandlerService bookingHandler;

	public BookingController() {
	}

	@GetMapping(value = "")
	public List<Booking> getBookings(
	@RequestParam(required = false) String startTime,
	@RequestParam(required = false) String endTime,
	@RequestParam(required = false) String date) {
		if (startTime != null && endTime != null) {
			LocalDateTime parsedStartTime =
			DateTimeHandler.parseDateTime(startTime,
			dateTimeFormat);
			LocalDateTime parsedEndTime = DateTimeHandler.parseDateTime(endTime,
			dateTimeFormat);
			return bookingRepository.getBookingsDuringTime(parsedStartTime,
			parsedEndTime);
		} else if (startTime != null) {
			LocalDateTime parsedStartTime = DateTimeHandler.parseDateTime(
			startTime,
			dateTimeFormat);
			return bookingRepository.getBookingsByStartTime(parsedStartTime);
		} else if (date != null) {
			LocalDateTime parsedDateTime = DateTimeHandler.parseDate(date,
			dateFormat).atStartOfDay();
			LocalDateTime nextDay = parsedDateTime.plusDays(1);

			return bookingRepository.getBookingsBetweenDates(parsedDateTime,
			nextDay);
		} else {
			return bookingRepository.findAll();
		}
	}

	@GetMapping("/{bookingId}")
	public Booking getBookingById(@PathVariable long bookingId) {
		return bookingRepository.findById(bookingId)
		.orElseThrow(() -> new BookingNotFoundException(bookingId));
	}

	@PutMapping("/{bookingId}")
	public ResponseEntity<?> updateBooking(
	@RequestBody Booking newBooking,
	@PathVariable long bookingId
	) {

		Optional<Booking> result =
		bookingRepository.findById(bookingId);

		if (result.isPresent()) {
			Booking booking = result.get();
			booking.updateBooking(newBooking);
			return new ResponseEntity<>("Booking sucessfully updated.", HttpStatus.NO_CONTENT);
		} else {
			ResponseEntity<?> response =
			this.createBooking(
			new RequestBodyUserBooking(newBooking.getUser(),
			newBooking));
			if (response.getStatusCode() == HttpStatus.CREATED) {
				return ResponseEntity.created(
				ServletUriComponentsBuilder
				.fromCurrentRequest().build().toUri())
				.build();
			}
			return response;
		}
	}

	@PostMapping("")
	public ResponseEntity<?> createBooking(
	@RequestBody RequestBodyUserBooking body) {

		Booking booking = body.getBooking();
		Optional<ResponseEntity<ApiError>> bookingValidationException =
		BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			return bookingValidationException.get();
		}

		User user = body.getUser();
		if (user.getEmail() == null) {
			return new ResponseEntity<>("Email is required.", HttpStatus.BAD_REQUEST);
		}
		Optional<Booking> result = bookingHandler.createBooking(booking, user);
		if (result.isPresent()) {
			return buildUriFromBooking(booking);
		} else {
			return new ResponseEntity<>("User has already made a booking on this date", HttpStatus.CONFLICT);
		}
	}

	private ResponseEntity<String> buildUriFromBooking(Booking booking) {
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest()
		.path("/{id}")
		.buildAndExpand(booking.getId())
		.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<String> deleteBooking(@PathVariable long bookingId) {
		bookingRepository.deleteById(bookingId);
		return ResponseEntity.noContent().build();
	}
}
