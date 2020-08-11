/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final DateTimeFormatter dateFormat = GlobalVariables.getDateFormat();
	private final DateTimeFormatter dateTimeFormat =
	GlobalVariables.getDateTimeFormat();
	@Autowired
	private Restaurant restaurant;
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

	@GetMapping("/dailyCount")
	public ResponseEntity<?> getBookingsPerDay() {
		Map<LocalDate, Integer> map = restaurant.getBookingsPerDate();
		return ResponseEntity.ok(restaurant.getBookingsPerDate());
	}

	@GetMapping("/{bookingId}")
	public Booking getBookingById(@PathVariable long bookingId) {
		return bookingRepository.findById(bookingId)
		.orElseThrow(() -> new BookingNotFoundException(bookingId));
	}

	@PutMapping("/{bookingId}")
	public void updateBooking(
	@RequestBody Booking newBooking,
	@PathVariable long bookingId,
	HttpServletRequest request,
	HttpServletResponse response
	) {

		Optional<Booking> result =
		bookingRepository.findById(bookingId);

		if (result.isPresent()) {
			Booking booking = result.get();
			booking.updateBooking(newBooking);
			try {
				sendResponse(response, HttpStatus.NO_CONTENT.value(), "Booking successfully " +
				"updated.");
			} catch (IOException e) {
				e.printStackTrace();
			}

			// TODO: update cache for date availability and number of bookings per day

		} else {
			this.createBooking(
			new RequestBodyUserBooking(newBooking.getUser(),
			newBooking), response);
		}
	}

	public void sendResponse(HttpServletResponse response, ResponseEntity<?> entity) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setStatus(entity.getStatusCodeValue());
		writer.print(entity.getBody());
		writer.flush();
		writer.close();
	}

	public <T> void sendResponse(HttpServletResponse response, int status,
								 T body) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setStatus(status);
		writer.print(body);
		writer.flush();
		writer.close();
	}

	@PostMapping("")
	public void createBooking(
	@RequestBody RequestBodyUserBooking body,
	HttpServletResponse response) {

		Booking booking = body.getBooking();
		Optional<ResponseEntity<ApiError>> bookingValidationException =
		BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			try {
				sendResponse(response, bookingValidationException.get());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		User user = new User(body.getUser());
		if (user.getUsername() == null) {
			throw new BookingRequestFormatException("Email is a required field");
		}
		Booking result = bookingHandler.createBooking(booking, user);
		try {
			sendResponse(response, 201, buildUriFromBooking(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
		restaurant.removeDateIfUnavailable(result.getStartTime().toLocalDate());
		restaurant.addBookingToDate(result.getDate(), result.getPartySize());
	}

	private ResponseEntity<String> buildUriFromBooking(Booking booking) {
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest()
		.replacePath("/bookings")
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
