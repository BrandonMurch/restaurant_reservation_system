/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.controller.BookingController;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.restaurants.controller.RestaurantController;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.controller.UserController;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RestaurantReservationSystemApplicationTests {

	@Autowired
	private BookingController bookingController;
	@Autowired
	private RestaurantController restaurantController;
	@Autowired
	private UserController userController;
	@Autowired
	private WebTestClient testClient;

	@BeforeEach
	void setup() {
	}

	@Test
	void getBookings() {
		List<Booking> bookings = testClient
		.get().uri("/bookings")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(Booking.class).returnResult().getResponseBody();

		if (bookings != null) {
			assertTrue(bookings.size() > 0);
		} else {
			fail();
		}
	}

	@Test
	void createBooking() {
		List<Booking> bookings = testClient
		.get().uri("/bookings")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(Booking.class).returnResult().getResponseBody();

		int size = 0;
		if (bookings != null) {
			size = bookings.size();
		}
		Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
		User user = CreateUsersForTesting.createUser1();
		RequestBodyUserBooking body = new RequestBodyUserBooking(user, booking);

		testClient
		.post().uri("/bookings")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.bodyValue(body)
		.exchange()
		.expectStatus().isCreated()
		.expectBody().isEmpty();
		testClient
		.get().uri("/bookings")
		.exchange()
		.expectBodyList(Booking.class).hasSize(size + 1);

	}

	@Test
	void deleteBooking() {
		List<Booking> bookings = testClient
		.get().uri("/bookings")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(Booking.class).returnResult().getResponseBody();

		int size = 0;
		if (bookings != null) {
			size = bookings.size();
		}

		testClient
		.delete().uri("/bookings/10000")
		.exchange()
		.expectStatus().is2xxSuccessful();

		testClient
		.get().uri("/bookings")
		.exchange()
		.expectBodyList(Booking.class).hasSize(size - 1);
	}

	@Test
	void getUsers() {
		List<User> users = testClient
		.get().uri("/users")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(User.class).returnResult().getResponseBody();

		if (users != null) {
			assertTrue(users.size() > 0);
		}
	}

	@Test
	void createUser() {
		List<User> users = testClient
		.get().uri("/users")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(User.class).returnResult().getResponseBody();

		int size = 0;
		if (users != null) {
			size = users.size();
		}
		User user = CreateUsersForTesting.createUser2();
		System.out.println(size);

		testClient
		.post().uri("/users")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.bodyValue(user)
		.exchange()
		.expectStatus().isCreated()
		.expectBody().isEmpty();
		testClient
		.get().uri("/users")
		.exchange()
		.expectBodyList(User.class).hasSize(size + 1);

	}

	@Test
	void deleteUser() {
		List<User> users = testClient
		.get().uri("/users")
		.exchange()
		.expectStatus().isOk()
		.expectBodyList(User.class).returnResult().getResponseBody();

		int size = 0;
		if (users != null) {
			size = users.size();
		}

		testClient
		.delete().uri("/users/10001")
		.exchange()
		.expectStatus().is2xxSuccessful();

		testClient
		.get().uri("/users")
		.exchange()
		.expectBodyList(Booking.class).hasSize(size - 1);
	}


}
