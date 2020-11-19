/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDate;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
	@Autowired
	private Restaurant restaurant;
	@Autowired
	private TableAllocatorService tableAllocator;
	private final DateTimeFormatter dateFormat =
	GlobalVariables.getDateFormat();
	private DateTimeHandler dateTimeHandler;

	public RestaurantController() {
	}

	private ResponseEntity<String> getAvailableDates() {
		JSONObject json = new JSONObject();
		DateRange range =
		restaurant.getBookingDateRange();
		json.put("start", range.getStart());
		json.put("end", range.getEnd());
		json.put("availableDates", restaurant.getAvailableDates());

		return new ResponseEntity<>(json.toString(),
		HttpStatus.OK);
	}

	@GetMapping(value = "/availability")
	public ResponseEntity<?> getAvailableBookingTimes(
	@RequestParam(required = false) String date,
	@RequestParam(required = false) Integer size) {

		if (date != null && size != null) {
			LocalDate parsedDate = parseDate(date, dateFormat);
			Set<LocalTime> set = tableAllocator.getAvailableTimes(size, parsedDate);
			return new ResponseEntity<>(set, HttpStatus.OK);
		} else {
			return getAvailableDates();
		}
	}

	@GetMapping(value = "/largest-table")
	public ResponseEntity<?> getTableSizes() {
		return new ResponseEntity<>(restaurant.getLargestTableSize(), HttpStatus.OK);
	}

	@GetMapping(value = "/tables")
	public ResponseEntity<?> getAllTables() {

		return new ResponseEntity<>(restaurant.getTableList(), HttpStatus.OK);
	}

	@PostMapping(value = "/tables")
	public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
		restaurant.addTable(table);
		return buildUriFromTable(table);
	}

	@PutMapping(value = "/tables")
	public ResponseEntity<?> updateTablePriorities(@RequestBody List<RestaurantTable> updatedTables) {
		restaurant.updateAllTables(updatedTables);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/tables/{name}")
	public ResponseEntity<?> updateTable(@RequestBody RestaurantTable newTable, @PathVariable String name) {
		Optional<RestaurantTable> result = restaurant.getTable(name);
		if (result.isPresent()) {
			RestaurantTable table = result.get();
			restaurant.updateTable(table, newTable);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping(value = "/tables/{name}")
	public ResponseEntity<?> deleteTable(@PathVariable String name) {
		restaurant.removeTable(name);
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<String> buildUriFromTable(RestaurantTable table) {
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest()
		.replacePath("/tables")
		.path("/{id}")
		.buildAndExpand(table.getName())
		.toUri();
		return ResponseEntity.created(location).build();
	}

	// admin only controller options - to be implemented in future
	// GET /RESTAURANTS/{RESTAURANT} - get a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES - get all tables of a restaurant
	// GET /RESTAURANTS/{RESTAURANT}/TABLES/{ID} - get one table
	// POST /RESTAURANTS/ - create a restaurant
	// POST /RESTAURANTS/{RESTAURANT}/ TABLES - create  a table
	// PUT /RESTAURANTS/{RESTAURANT} - update a restaurant
	// PUT /RESTAURANTS/{RESTAURANT}/TABLES/{TABLE} - update a table

}
