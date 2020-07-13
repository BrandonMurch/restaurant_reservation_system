package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.helpers.http.HttpRequestBuilder;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

	public Optional<Booking> createBooking(Booking booking, User user) {
		Optional<User> result = handleUsersForBooking(user,
				booking.getStartTime().toLocalDate());
		if (result.isEmpty()) {
			return Optional.empty();
		} else {
			booking.setUser(result.get());
		}

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
		tableAllocatorService.removeDateIfUnavailable(date);

		return Optional.of(booking);
	}

	private Optional<User> handleUsersForBooking(User user, LocalDate date) {
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
					return Optional.empty();
				}
			}
		}
		return Optional.of(user);
	}
}
