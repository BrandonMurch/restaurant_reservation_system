/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.jsonToObject;
import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {

	@MockBean
	private BookingRepository bookingRepository;
	@MockBean
	private BookingHandlerService bookingHandler;
	@MockBean
	private Restaurant restaurant;
	@Autowired
	private MockMvc mvc;

	private List<Booking> bookings;
	private Booking booking1;
	private Booking updatedBooking2;


	@BeforeEach
	void setUp() {
		bookings = initBookings();
	}

	private List<Booking> initBookings() {
		CreateBookingsForTest createBooking = new CreateBookingsForTest();
		booking1 = createBooking.createBookingForTwoAt19();
		User user = booking1.getUser();
		Booking booking2 = createBooking.createBookingForFourAt20();
		updatedBooking2 = createBooking.createUpdatedBookingForFour();
		return Arrays.asList(booking1, booking2);

	}

	@Test
	void getBookings() throws Exception {
		Mockito.when(bookingRepository.findAll()).thenReturn(this.bookings);

		String uri = "/bookings";
		MvcResult result =
				mvc.perform(MockMvcRequestBuilders
						.get(uri)
						.contentType(MediaType.APPLICATION_JSON))
						.andReturn();
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		String content = result.getResponse().getContentAsString();
		Booking[] bookings = jsonToObject(content, Booking[].class);
		assertTrue(bookings.length > 0);
		assertEquals(2, bookings[0].getPartySize());
	}

	@Test
	void getBookingsByTime() throws Exception {

		String start = "2020-10-11T18:00:00.00";
		String end = "2020-10-11T21:00:00.00";
		LocalDateTime startTime = LocalDateTime.parse(start);
		LocalDateTime endTime = LocalDateTime.parse(end);
		Mockito.when(bookingRepository
				.getBookingsDuringTime(startTime, endTime))
				.thenReturn(this.bookings);

		String uri = "/bookings?startTime=" + start + "&endTime=" + end;
		MvcResult result =
				mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
						MediaType.APPLICATION_JSON)).andReturn();
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		String content = result.getResponse().getContentAsString();
		Booking[] bookings = jsonToObject(content, Booking[].class);
		assertEquals(2, bookings.length);
	}

	@Test
	void getBookingsByStartTime() throws Exception {
		String start = "2020-10-11T20:00";
		LocalDateTime startTime = LocalDateTime.parse(start);
		Mockito.when(bookingRepository
				.getBookingsByStartTime(startTime))
				.thenReturn(this.bookings.stream()
						.filter(booking -> booking.getStartTime().equals(
								startTime))
						.collect(Collectors.toList()));

		String uri = "/bookings?startTime=" + start;
		MvcResult result =
				mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
						MediaType.APPLICATION_JSON)).andReturn();
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		String content = result.getResponse().getContentAsString();
		Booking[] bookings = jsonToObject(content, Booking[].class);
		assertEquals(1, bookings.length);
	}

	@Test
	void getBookingsByDate() throws Exception {
		String date = "2020-12-11";
		LocalDateTime startDate = LocalDate.parse(date).atStartOfDay();
		LocalDateTime endDate = startDate.plusDays(1);
		Mockito.when(bookingRepository
				.getBookingsBetweenDates(startDate, endDate))
				.thenReturn(this.bookings);

		String uri = "/bookings?date=" + date;
		MvcResult result =
				mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
						MediaType.APPLICATION_JSON)).andReturn();
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		String content = result.getResponse().getContentAsString();
		Booking[] bookings = jsonToObject(content, Booking[].class);
		assertEquals(2, bookings.length);

	}


	@Test
	void getBookingById() throws Exception {
		Mockito.when(bookingRepository.findById((long) 1)).thenReturn(
				Optional.ofNullable(this.booking1));

		String uri = "/bookings/1";
		MvcResult result =
				mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
						MediaType.APPLICATION_JSON)).andReturn();
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		String content = result.getResponse().getContentAsString();
		Booking booking = jsonToObject(content, Booking.class);
		assertEquals(booking.getPartySize(), booking1.getPartySize());

	}

	@Test
	void updateBookingNotPresent() throws Exception {
		Mockito.when(bookingRepository.findById((long) 1))
		.thenReturn(Optional.empty());
		Mockito.when(bookingHandler.createBooking(any(Booking.class), any(User.class)))
		.thenReturn(Optional.of(updatedBooking2));

		String uri = "/bookings/" + updatedBooking2.getId();
		String bookingJson = objectToJson(updatedBooking2);
		MvcResult result =
		mvc.perform(MockMvcRequestBuilders.put(uri)
		.accept(MediaType.APPLICATION_JSON)
		.content(bookingJson)
		.contentType(MediaType.APPLICATION_JSON))
		.andReturn();

		assertEquals(201, result.getResponse().getStatus());
	}

	@Test
	void updateBookingAlreadyPresent() throws Exception {
		Mockito.when(bookingRepository.findById(any(Long.class)))
		.thenReturn(Optional.of(this.updatedBooking2));

		String uri = "/bookings/" + updatedBooking2.getId();
		String bookingJson = objectToJson(updatedBooking2);
		MvcResult result =
		mvc.perform(MockMvcRequestBuilders.put(uri)
		.accept(MediaType.APPLICATION_JSON)
		.content(bookingJson)
		.contentType(MediaType.APPLICATION_JSON))
		.andReturn();

		assertEquals(204, result.getResponse().getStatus());


	}
}