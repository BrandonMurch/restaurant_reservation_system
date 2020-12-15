/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.parseDate;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableService;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private Restaurant restaurant;
	@Autowired
	private TableService tableService;
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
		return new ResponseEntity<>(tableService.getLargestTableSize(), HttpStatus.OK);
	}

	@GetMapping(value = "/tables")
	public ResponseEntity<?> getAllTables() {

		return new ResponseEntity<>(tableService.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/tables")
	public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
		tableService.add(table);
		return buildUriFromTable(table);
	}

	@PostMapping(value = "/combinations")
	public ResponseEntity<?> createCombination(@RequestBody String tables) {
		CombinationOfTables created = tableService.createCombination(tables);
		return buildUriFromTable(created);
	}

	@PutMapping(value = "/tables")
	public ResponseEntity<?> updateTablePriorities(@RequestBody List<RestaurantTable> updatedTables) {
		tableService.updateAll(updatedTables);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/tables/{name}")
	public ResponseEntity<?> updateTable(@RequestBody RestaurantTable newTable, @PathVariable String name) {
		tableService.update(name, newTable);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/tables/{name}")
	public ResponseEntity<?> deleteTable(@PathVariable String name) {
		tableService.remove(name);
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
	// POST /RESTAURANTS/ - create a restaurant
	// PUT /RESTAURANTS/{RESTAURANT} - update a restaurant

}
