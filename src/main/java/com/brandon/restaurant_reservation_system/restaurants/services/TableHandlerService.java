/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TableHandlerService {
	@Autowired
	private TableRepository tableRepository;

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

	public void update(RestaurantTable existing, RestaurantTable updated) {
		existing.update(updated);
		tableRepository.save(existing);
	}


	public int remove(String name) {
		Optional<RestaurantTable> result = tableRepository.findById(name);
		return result.map(restaurantTable -> tableRepository.deleteWithAssociatedCombinations(restaurantTable)).orElse(0);
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
