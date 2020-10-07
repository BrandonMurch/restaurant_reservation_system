/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableHandlerService;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
	@Autowired
	private TableAvailabilityService tableAvailability;
	@Autowired
	private TableHandlerService tableHandler;

	public BookingController() {
	}

	private List<Booking> parseDateTimesAndFindBookings(String start,
														String end) {
		LocalDateTime parsedStartTime =
		DateTimeHandler.parseDateTime(start,
		dateTimeFormat);
		LocalDateTime parsedEndTime = DateTimeHandler.parseDateTime(end,
		dateTimeFormat);
		return bookingRepository.getBookingsDuringTime(parsedStartTime,
		parsedEndTime);
	}

	private List<Booking> parseDateAndFindBookings(String date) {
		LocalDateTime parsedDateTime = DateTimeHandler.parseDate(date,
		dateFormat).atStartOfDay();
		LocalDateTime nextDay = parsedDateTime.plusDays(1);

		return bookingRepository.getBookingsBetweenDates(parsedDateTime,
		nextDay);
	}

	private List<Booking> parseStartTimeAndFindBookings(String start) {
		LocalDateTime parsedStartTime = DateTimeHandler.parseDateTime(
		start, dateTimeFormat);
		return bookingRepository.getBookingsByStartTime(parsedStartTime);
	}

	@GetMapping(value = "")
	public List<Booking> getBookings(
	@RequestParam(required = false) String startTime,
	@RequestParam(required = false) String endTime,
	@RequestParam(required = false) String date) {
		if (startTime != null && endTime != null) {
			return parseDateTimesAndFindBookings(startTime, endTime);
		} else if (startTime != null) {
			return parseStartTimeAndFindBookings(startTime);
		} else if (date != null) {
			return parseDateAndFindBookings(date);
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

	// TODO: allow update to be forced, switching tables between bookings if necessary
	@PutMapping("{bookingId}/setTable/{tableNames}")
	public void updateBookingWithTable(@PathVariable long bookingId,
									   @PathVariable String tableNames,
									   HttpServletResponse response) {
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		if (optionalBooking.isEmpty()) {
			throw new BookingNotFoundException("Booking Id was not found");
		}

		Booking booking = optionalBooking.get();
		if (tableNames.equals("")) {
			booking.setTables(Collections.emptyList());
		}

		List<RestaurantTable> tables;
		try {
			tables = tableHandler.find(tableNames);
		} catch (TableNotFoundException exception) {
			throw new BookingNotPossibleException(exception.getMessage());
		}

		if (!tableAvailability.areTablesFree(tables,
		booking.getStartTime(), booking.getEndTime())) {
			throw new BookingNotPossibleException("Table is already taken");
		}
		booking.setTables(tables);
		bookingRepository.save(booking);

		try {
			sendResponse(response, HttpStatus.NO_CONTENT.value(), "Booking " +
			"table successfully updated.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new InternalError("Response sending failed");
		}
	}

	@PutMapping("/{bookingId}")
	public void updateBooking(
	@RequestBody Booking newBooking,
	@PathVariable long bookingId,
	HttpServletRequest request,
	HttpServletResponse response
	) throws HttpServerErrorException.InternalServerError {
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
				throw new InternalError("Response sending failed");
			}

			Boolean hasDateChanged = !newBooking.getDate().isEqual(booking.getDate());
			Boolean hasSizeChanged = !newBooking.getPartySize().equals(booking.getPartySize());
			if (hasDateChanged || hasSizeChanged) {
				restaurant.removeBookingFromDate(booking.getDate(), booking.getPartySize());
				restaurant.addBookingToDate(newBooking.getDate(), newBooking.getPartySize());
			}
		} else {
			createBooking(
			new RequestBodyUserBooking(newBooking.getUser(), newBooking),
			response);
		}
	}

	@PostMapping("")
	public void createBooking(
	@RequestBody RequestBodyUserBooking body,
	HttpServletResponse response) {

		Booking booking = body.getBooking();
		Optional<ApiError> bookingValidationException =
		BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			try {
				ApiError apiError = bookingValidationException.get();
				sendResponse(response, new ResponseEntity<>(apiError,
				apiError.getStatus()));
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
			sendResponse(response, buildUriFromBooking(result));
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
		Optional<Booking> booking = bookingRepository.findById(bookingId);
		booking.ifPresent(booking1 -> restaurant.removeBookingFromDate(booking1.getDate(), booking1.getPartySize()));
		bookingRepository.deleteById(bookingId);
		return ResponseEntity.noContent().build();
	}

	private void sendResponse(HttpServletResponse response, ResponseEntity<?> entity) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setStatus(entity.getStatusCodeValue());
		writer.print(entity);
		writer.flush();
		writer.close();
	}

	private void sendResponse(HttpServletResponse response, int status,
							  Object body) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setStatus(status);
		writer.print(body);
		writer.flush();
		writer.close();
	}

}
