/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.DuplicateFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class BookingHandlerService {
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TableAllocatorService tableAllocatorService;

	public BookingHandlerService() {
	}

	public void freeTablesIfForcedOrSame(Booking booking, List<RestaurantTable> tables,
										 boolean isForced) {
		Set<Booking> bookingsOccupyingTables =
		bookingRepository.getBookingsByTimeAndMultipleTables(
		booking.getStartTime(),
		booking.getEndTime(),
		tables);
		bookingsOccupyingTables.forEach((bookingToEmpty) -> {
			if (!bookingToEmpty.equals(booking)) {
				if (isForced) {
					bookingToEmpty.setTables(Collections.emptyList());
					bookingRepository.save(bookingToEmpty);
				} else {
					throw new BookingNotPossibleException("Table is already taken. \n " +
					"(Forcing this will remove the desired table from other bookings)", true);

				}
			}
		});
	}

	public Booking updateBooking(Booking booking, Booking newBooking,
								 boolean isForced) throws Exception {
		Booking oldBooking;
		try {
			oldBooking = booking.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Exception("Internal Server Error");
		}
		booking.updateBooking(newBooking);

		Optional<ApiError> bookingValidationException =
		BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			booking.updateBooking(oldBooking);
			throw new BookingRequestFormatException(bookingValidationException.get());
		}

		if (!booking.getStartTime().isEqual(newBooking.getStartTime())) {
			List<RestaurantTable> tables =
			tableAllocatorService.getAvailableTable(booking);
			if (tables.isEmpty()) {
				if (!isForced) {
					booking.updateBooking(oldBooking);
					throw new BookingNotPossibleException("Requested date is not " +
					"available", true);
				}
			}
			booking.setTables(tables);
			booking.setDate(booking.getStartTime().toLocalDate());

		}

		bookingRepository.save(booking);
		return booking;


	}

	public Booking createBooking(Booking booking, User user, boolean isForced) {

		User result = handleUsersForBooking(user,
		booking.getStartTime().toLocalDate());
		booking.setUser(result);

		List<RestaurantTable> tables =
		tableAllocatorService.getAvailableTable(booking);
		if (tables.isEmpty()) {
			if (!isForced) {
				throw new BookingNotPossibleException("Requested date is not " +
				"available", true);
			}
		}
		booking.setTables(tables);

		booking.setDate(booking.getStartTime().toLocalDate());
		bookingRepository.save(booking);
		return booking;
	}

	private User handleUsersForBooking(User user, LocalDate date) {
		Optional<User> dbUser = userRepository.findByEmail(user.getEmail());

		if (dbUser.isEmpty()) {
			userRepository.save(user);
		} else {
			user = dbUser.get();
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
