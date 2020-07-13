package com.brandon.restaurant_reservation_system.restaurants.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
	@Autowired
	private Restaurant restaurant;
	@Autowired
	private TableAllocatorService tableAllocator;
	@Autowired
	private BookingRepository bookingRepository;
	DateTimeFormatter dateFormat =
			GlobalVariables.getDateFormat();
	private DateTimeHandler dateTimeHandler;

	public RestaurantController() {
	}

	@GetMapping("/availability")
	public ResponseEntity<String> getDate() {
		JSONObject json = new JSONObject();
		DateRange range =
				restaurant.getBookingDateRange();
		json.put("start", range.getStart());
		json.put("end", range.getEnd());
		json.put("availableDates", restaurant.getAvailableDates());

		return new ResponseEntity<>(json.toString(),
				HttpStatus.OK);
	}

	@GetMapping(value = "/availability", params = {"date", "size"})
	public Set<LocalTime> getAvailableBookingTimes(
			@RequestParam String date,
			@RequestParam int size) {
		LocalDate parsedDate = parseDate(date, dateFormat);
		return tableAllocator.getAvailableTimes(size, parsedDate);
	}

	// admin only controller options
	// GET /RESTAURANTS/{RESTAURANT} - get a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES - get all tables of a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES/{ID} - get one table
	// POST /RESTAURANTS/ - create a restaurant
	// POST /RESTAURANTS/{RESTAURANT}/ TABLES - create  a table
	// PUT /RESTAURANTS/{RESTAURANT} - update a restaurant
	// PUT /RESTAURANTS/{RESTAURANT}/TABLES/{TABLE} - update a table

}
