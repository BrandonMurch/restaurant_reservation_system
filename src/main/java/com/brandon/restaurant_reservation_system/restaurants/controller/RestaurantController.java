package com.brandon.restaurant_reservation_system.restaurants.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantStub;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDate;

public class RestaurantController {

	private final Restaurant restaurant;
	private final TableAllocatorService tableAllocator;
	DateTimeFormatter dateFormat =
			GlobalVariables.getDateFormat();
	private DateTimeHandler dateTimeHandler;

	public RestaurantController() {
		RestaurantStub restaurantStub = new RestaurantStub();
		restaurant = restaurantStub.getRestaurant();
		tableAllocator = new TableAllocatorService(restaurant);

	}
	//TEMPORARY STUBS

	//GET /restaurant/dates
	@GetMapping("/bookings/free-dates")
	public JSONObject getDate() {
		JSONObject json = new JSONObject();
		DateRange range =
				restaurant.getBookingDateRange();
		json.put("start", range.getStart());
		json.put("end", range.getEnd());
		json.put("availableDates", tableAllocator.getAvailableDates());

		return json;
	}

	@GetMapping("/bookings/date/{date}/size/{size}")
	public Set<LocalTime> getAvailableBookingTimes(
			@PathVariable String dateString,
			@PathVariable int size) {
		LocalDate date = parseDate(dateString, dateFormat);
		return tableAllocator.getAvailableTimes(size, date);
	}

	//GET /restaurant/freeSizes


	// admin only controller options
	// GET /RESTAURANTS/{RESTAURANT} - get a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES - get all tables of a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES/{ID} - get one table
	// POST /RESTAURANTS/ - create a restaurant
	// POST /RESTAURANTS/{RESTAURANT}/ TABLES - create  a table
	// PUT /RESTAURANTS/{RESTAURANT} - update a restaurant
	// PUT /RESTAURANTS/{RESTAURANT}/TABLES/{TABLE} - update a table

}
