/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.DuplicateFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TableAllocatorService tableAllocatorService;
	@Autowired
	private TableService tableService;
	@Autowired
	private TableAvailabilityService tableAvailabilityService;

	public BookingService() {
	}

	public Booking find(Long id) {
		Optional<Booking> booking = bookingRepository.findById(id);
		if (booking.isEmpty()) {
			throw new BookingNotFoundException(id);
		}
		return booking.get();
	}

	public void updateTable(Booking booking, String tableName) {
		updateTable(booking, tableName, false);
	}


	public void updateTable(Booking booking, String tableName, Boolean isForced) {
		if (tableName.equals("")) {
			booking.setTables(Collections.emptyList());
		} else {
			RestaurantTable table;
			try {
				table = tableService.find(tableName);
			} catch (TableNotFoundException exception) {
				throw new BookingNotPossibleException(exception.getMessage());
			}

			if (!tableAvailabilityService.areTablesFree(table.getAssociatedTables(),
			booking.getStartTime(), booking.getEndTime())) {
				freeTablesIfForcedOrSame(booking, table.getAssociatedTables(), isForced);
			}

			if (booking.getPartySize() > table.getSeats()) {
				if (!isForced) {
					throw new BookingNotPossibleException("Table is not big enough for " +
					"party", true);
				}
			}

			booking.setTables(table.getAssociatedTables());
		}
		bookingRepository.save(booking);
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

	public List<Booking> freeTablesFromBookings(List<RestaurantTable> tablesToFree) {
		List<Booking> bookings = new ArrayList<>();
		List<Booking> bookingsThatCannotBeReassigned = new ArrayList<>();
		tablesToFree.forEach((table) -> bookings.addAll(bookingRepository.getFutureBookingsByTable(table.getName())));
		bookings.forEach((booking) -> {
			List<RestaurantTable> availableTables =
			tableAllocatorService.getAvailableTable(booking);
			if (availableTables.isEmpty()) {
				bookingsThatCannotBeReassigned.add(booking);
			} else {
				booking.setTables(availableTables);
			}
		});

		return bookingsThatCannotBeReassigned;
	}


	public void updateBooking(Booking booking, Booking newBooking,
							  boolean isForced) throws Exception {
		Booking oldBooking;
		try {
			oldBooking = booking.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Exception("Internal Server Error");
		}
		booking.update(newBooking);

		Optional<ApiError> bookingValidationException =
		BookingValidationService.validateBooking(booking);
		if (bookingValidationException.isPresent()) {
			booking.update(oldBooking);
			throw new BookingRequestFormatException(bookingValidationException.get());
		}

		if (!booking.getStartTime().isEqual(newBooking.getStartTime())) {
			List<RestaurantTable> tables =
			tableAllocatorService.getAvailableTable(booking);
			if (tables.isEmpty()) {
				if (!isForced) {
					booking.update(oldBooking);
					throw new BookingNotPossibleException("Requested date is not " +
					"available", true);
				}
			}
			booking.setTables(tables);
			booking.setDate(booking.getStartTime().toLocalDate());

		}

		bookingRepository.save(booking);


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
