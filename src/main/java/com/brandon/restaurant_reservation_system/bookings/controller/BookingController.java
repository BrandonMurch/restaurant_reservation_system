package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.helpers.http.HttpRequestBuilder;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
public class BookingController {

	private final DateTimeFormatter timeFormat = GlobalVariables.getDateTimeFormat();
	private final DateTimeFormatter dateFormat = GlobalVariables.getDateFormat();
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private HttpRequestBuilder httpRequestBuilder;

	public BookingController() {
	}

	@GetMapping("/bookings")
	public List<Booking> getBookings() {
		return bookingRepository.findAll();
	}

	@GetMapping("/bookings/time={startTime}-{endTime}")
	public List<Booking> getBookingsDuringTime(@PathVariable String startTime,
	                                           @PathVariable String endTime) {
		LocalDateTime parsedStartTime = DateTimeHandler.parseDateTime(startTime,
				timeFormat);
		LocalDateTime parsedEndTime = DateTimeHandler.parseDateTime(endTime,
				timeFormat);
		return bookingRepository.getBookingsDuringTime(parsedStartTime,
				parsedEndTime);
	}

	@GetMapping("/bookings/start-time={time}")
	public List<Booking> getBookingsByStartTime(@PathVariable String time) {
		LocalDateTime parsedStartTime = DateTimeHandler.parseDateTime(time,
				timeFormat);
		return bookingRepository.getBookingsByStartTime(parsedStartTime);
	}

	@GetMapping("/bookings/date={date}")
	public List<Booking> getBookingsByDate(@PathVariable String date) {
		LocalDate parsedDate;
		parsedDate = DateTimeHandler.parseDate(date,
				dateFormat);
		LocalDate nextDay = parsedDate.plusDays(1);

		return bookingRepository.getBookingsBetweenDates(parsedDate, nextDay);
	}

	@GetMapping("/users/{id}/bookings")
	public List<Booking> getBookingsByUser(@PathVariable long id) {
		// Calls to /users/{id} to get a user
		User user = getUser(id);
		return user.getBookings();
	}

	private User getUser(long id) {
		try {
			return httpRequestBuilder
					.httpGetUsers("/users/" + id).get(0);
		} catch (HttpClientErrorException ex) {
			throw new UserNotFoundException(ex.getResponseBodyAsString());
		}
	}

	@GetMapping("/bookings/{bookingId}")
	public Booking getBookingById(@PathVariable long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException(bookingId));
	}

	@PutMapping("/users/{userId}/bookings/{bookingId}")
	public ResponseEntity<?> updateBooking(
			@Valid @RequestBody Booking newBooking,
			@PathVariable long userId,
			@PathVariable long bookingId) {

		return bookingRepository.findById(bookingId)
				.map(booking -> {
					booking.updateBooking(newBooking);
					bookingRepository.save(booking);
					return getNoContentResponse();
				})
				.orElseGet(() -> {
					ResponseEntity<Booking> response =
							(ResponseEntity<Booking>) this.createBooking(newBooking, userId);
					if (response.getStatusCode() == HttpStatus.CREATED) {
						return ResponseEntity.created(
								ServletUriComponentsBuilder
										.fromCurrentRequest().build().toUri())
								.build();
					}
					return response;
				});
	}

	private ResponseEntity<Booking> getNoContentResponse() {
		return ResponseEntity.noContent().build();
	}

	// todo check that end time is allocated. If not, add the standard
	//  duration to start time
	@PostMapping("/users/{id}/bookings")
	public HttpEntity<?> createBooking(
			@Valid @RequestBody Booking booking, @PathVariable long id) {

		User user = getUser(id);
		booking.setUser(user);

		// todo move this to a private function??
		List<Booking> bookings = user.getBookings();
		LocalDate bookingDate = booking.getStartTime().toLocalDate();

		for (Booking storedBooking : bookings) {
			if (storedBooking.getStartTime().toLocalDate().equals(
					bookingDate)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		}

		Optional<ResponseEntity<ApiError>> bookingValidationException =
				BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			return bookingValidationException.get();
		}
		booking = bookingRepository.save(booking);
		return buildUriFromBooking(booking);
	}

	private ResponseEntity<Booking> buildUriFromBooking(Booking booking) {
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(booking.getId())
				.toUri();
		return ResponseEntity.created(location).build();

	}

	@DeleteMapping("/bookings/{bookingId")
	public ResponseEntity<Booking> deleteBooking(@PathVariable long bookingId) {
		bookingRepository.deleteById(bookingId);
		return getNoContentResponse();
	}
}
