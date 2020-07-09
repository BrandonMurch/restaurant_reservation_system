package com.brandon.restaurant_reservation_system.bookings;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.CreateRestaurantForTest;
import com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.model.User;

import java.time.format.DateTimeFormatter;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDateTime;

public class CreateBookingsForTest {

	private final CreateUsersForTesting createUser = new CreateUsersForTesting();
	private final User user = createUser.createUser1();
	private final User user2 = createUser.createUser2();
	private final DateTimeFormatter dateTimeFormat =
			GlobalVariables.getDateTimeFormat();
	private final Restaurant restaurant = CreateRestaurantForTest.create();
	private final CreateTableForTest createTableForTest =
			new CreateTableForTest(restaurant);

	public CreateBookingsForTest() {
	}

	public Booking createBookingForTwoAt19() {
		Booking booking = new Booking(2,
				parseDateTime("2020-10-11T19:00:00.00",
						dateTimeFormat),
				parseDateTime("2020-10-11T23:00:00.00",
						dateTimeFormat),
				user2);
		booking.setId(1);
		return booking;
	}

	public Booking createBookingForFourAt20() {
		Booking booking = new Booking(4,
				parseDateTime("2020-10-11T20:00:00.00",
						dateTimeFormat),
				parseDateTime("2020-10-11T23:00:00.00",
						dateTimeFormat),
				user);
		booking.setId(2);
		return booking;
	}

	public Booking createUpdatedBookingForFour() {
		Booking booking = new Booking(4,
				parseDateTime("2020-10-11T20:00:00.00", dateTimeFormat),
				parseDateTime("2020-10-11T18:00:00.00", dateTimeFormat), user);
		booking.setId(2);
		booking.addTable(createTableForTest.getTable1());
		return booking;

	}

	public Booking createBookingForFourAt19() {
		Booking booking = new Booking(4,
				parseDateTime("2020-10-11T19:00:00.00",
						dateTimeFormat),
				parseDateTime("2020-10-11T23:00:00.00",
						dateTimeFormat),
				user);
		booking.setId(3);
		return booking;
	}



}
