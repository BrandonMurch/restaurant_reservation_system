/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingHandlerService;
import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.SubErrorMessage;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.DuplicateTableFoundException;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.UnallocatedBookingTableException;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TableHandlerService {
	@Autowired
	private TableRepository tableRepository;
	@Autowired
	private BookingHandlerService bookingHandler;
	@Autowired
	TableAllocatorService tableAllocator;

	public TableHandlerService() {
	}

	// EITHER TABLES OR COMBINATIONS  ------------------------------------------

	public List<RestaurantTable> find(String tableNames) {
		String[] splitTableNames = tableNames.split(",");
		List<RestaurantTable> tableList = new ArrayList<>();
		if (splitTableNames.length > 1) {
			Optional<CombinationOfTables> optionalTables =
			getCombination(tableNames);
			if (optionalTables.isEmpty()) {
				throw new TableNotFoundException("Tables were not found");
			}
			return optionalTables.get().getTables();
		}
		Optional<RestaurantTable> optionalTable =
		get(splitTableNames[0]);
		if (optionalTable.isEmpty()) {
			throw new TableNotFoundException("Table is not found");
		}
		return Collections.singletonList(optionalTable.get());
	}

	// INDIVIDUAL TABLES -------------------------------------------------------

	public Optional<RestaurantTable> get(String name) {
		return tableRepository.findById(name);
	}

	public List<RestaurantTable> getAll() {
		return tableRepository.findAll();
	}

	public void add(String name, int seats) {
		int length = getTableCount();
		add(new RestaurantTable(name, seats, length));
	}

	public void add(RestaurantTable table) {
		if (table.getPriority() == -1) {
			table.setPriority(getTableCount());
		}
		tableRepository.save(table);
	}

	public void addAll(List<RestaurantTable> restaurantTableList) {
		tableRepository.saveAll(restaurantTableList);
	}

	public void update(String name, RestaurantTable updated) {
		var result = tableRepository.findById(name);
		if (result.isEmpty()) {
			throw new TableNotFoundException(name);
		}
		RestaurantTable existing = result.get();
		existing.update(updated);
	}

	public void updateAll(List<RestaurantTable> tables) {
		tables.forEach(table -> this.update(table.getName(), table));
	}

	public void remove(String name) {
		Optional<RestaurantTable> result = tableRepository.findById(name);
		if (result.isPresent()) {
			RestaurantTable table = result.get();
			List<RestaurantTable> tablesAndCombinations = new ArrayList<>();
			tablesAndCombinations.add(table);
			tablesAndCombinations.addAll(tableRepository.findAssociatedCombinations(table));
			List<Booking> bookings =
			bookingHandler.freeTablesFromBookings(tablesAndCombinations);
			tableRepository.deleteWithAssociatedCombinations(table);
			if (!bookings.isEmpty()) {
				throw new UnallocatedBookingTableException(
				createApiErrorRemovingTables(bookings)
				);
			}
		} else {
			throw new TableNotFoundException(name);
		}
	}

	private ApiError createApiErrorRemovingTables(List<Booking> bookings) {
		ApiError apiError = new ApiError(HttpStatus.CONFLICT, "Bookings have " +
		"been left without a table.");
		bookings.forEach((booking) -> apiError.addSubError(new SubErrorMessage(booking.toString(),
		" was not able to be reassigned")));
		return apiError;
	}

	// TABLE COMBINATIONS ------------------------------------------------------


	public List<CombinationOfTables> getAllCombinations() {
		return tableRepository.findAllCombinations();
	}

	public Optional<CombinationOfTables> getCombination(String name) {
		return tableRepository.findCombinationByName(name);
	}

	public void createCombination(List<RestaurantTable> tables) {
		int priority = getTableCount();
		CombinationOfTables combination = new CombinationOfTables(tables, priority);
		tableRepository.save(combination);
	}

	public CombinationOfTables createCombination(String tableNames) {
		String[] splitTableNames = tableNames.split(", ");
		Arrays.sort(splitTableNames);
		List<RestaurantTable> tables = new ArrayList<>();
		for (String tableName : splitTableNames) {
			var result = tableRepository.findById(tableName);
			result.ifPresentOrElse(tables::add, () -> {
				throw new TableNotFoundException(tableName);
			});
		}
		int priority = getTableCount();
		CombinationOfTables combination = new CombinationOfTables(tables, priority);
		var result = tableRepository.findById(combination.getName());
		if (result.isPresent()) {
			throw new DuplicateTableFoundException(combination.getName());
		}
		return tableRepository.save(combination);
	}

	//	Largest RestaurantTable Size  --------------------------------------------------------

	public int getLargestTableSize() {
		return tableRepository.getLargestTableSize();
	}

	// Other
	public boolean willPartyFitOnTable(int partySize, List<RestaurantTable> tables) {
		int size = 0;
		for (RestaurantTable table : tables) {
			size += table.getSeats();
		}
		return size >= partySize;
	}

	private int getTableCount() {
		return (int) tableRepository.count();
	}


}
