/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.DuplicateFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.helpers.http.HttpRequestBuilder;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookingHandlerService {
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TableAllocatorService tableAllocatorService;
	@Autowired
	private HttpRequestBuilder httpRequestBuilder;

	public BookingHandlerService() {
	}

	public Booking createBooking(Booking booking, User user) {
		User result = handleUsersForBooking(user,
		booking.getStartTime().toLocalDate());
		booking.setUser(result);

		List<RestaurantTable> tables =
		tableAllocatorService.getAvailableTable(booking);
		if (tables.isEmpty()) {
			throw new BookingNotPossibleException("Requested date is not " +
			"available");
		}
		booking.setTables(tables);
		tables.forEach((table) -> table.addBooking(booking));

		bookingRepository.save(booking);

		LocalDate date = booking.getStartTime().toLocalDate();
		return booking;
	}

	private User handleUsersForBooking(User user, LocalDate date) {
		List<User> dbUsers;
		try {
			dbUsers = httpRequestBuilder.httpGetUsers("/users" +
			"?email=" + user.getEmail());
		} catch (HttpClientErrorException ex) {
			throw new UserNotFoundException(ex.getResponseBodyAsString());
		}
		if (dbUsers.isEmpty()) {
			userRepository.save(user);
		} else {
			user = dbUsers.get(0);
			List<Booking> bookings =
			bookingRepository.getBookingsByUser(user.getEmail());

			for (Booking storedBooking : bookings) {
				if (storedBooking.getStartTime().toLocalDate().equals(
				date)) {
					throw new DuplicateFoundException("You have already made a booking " +
					"on this date");
				}
			}
		}
		return user;
	}
}
