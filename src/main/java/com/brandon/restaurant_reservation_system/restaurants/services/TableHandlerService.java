/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.CombinationRepository;
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
	@Autowired
	private CombinationRepository combinationRepository;
	private int largestTableSize = 0;

	public TableHandlerService() {
	}

	public List<RestaurantTable> find(String tableNames) {
		String[] splitTableNames = tableNames.split(",");
		List<RestaurantTable> tableList = new ArrayList<>();
		// FIXME: if the name isn't in the same order, the search won't work...
		if (splitTableNames.length > 1) {
			Optional<CombinationOfTables> optionalTables =
			getCombination(tableNames);
			if (optionalTables.isEmpty()) {
				throw new TableNotFoundException("Tables were not found");
			}
			return optionalTables.get().getRestaurantTables();
		}
		Optional<RestaurantTable> optionalTable =
		get(splitTableNames[0]);
		if (optionalTable.isEmpty()) {
			throw new TableNotFoundException("Table is not found");
		}
		return Collections.singletonList(optionalTable.get());
	}

	// INDIVIDUAL TABLES -------------------------------------------------------

	public List<RestaurantTable> getAll() {
		return tableRepository.findAll();
	}

	public void setAll(List<RestaurantTable> restaurantTableList) {
		tableRepository.saveAll(restaurantTableList);
	}

	public Optional<RestaurantTable> get(String name) {
		return tableRepository.findById(name);
	}

	public void add(String name, int seats) {
		tableRepository.save(new RestaurantTable(name, seats));
		updateLargestTableSize();
	}

	protected void updateLargestTableSize() {
		for (RestaurantTable restaurantTable : tableRepository.findAll()) {
			if (restaurantTable.getSeats() > this.largestTableSize) {
				this.largestTableSize = restaurantTable.getSeats();
			}
		}

		for (CombinationOfTables table : combinationRepository.findAll()) {
			if (table.getTotalSeats() > this.largestTableSize) {
				this.largestTableSize = table.getTotalSeats();
			}
		}
	}

	public void remove(String name) {
		tableRepository.deleteById(name);
		updateLargestTableSize();
	}

	// TABLE COMBINATIONS ------------------------------------------------------


	public List<CombinationOfTables> getAllCombinations() {
		return combinationRepository.findAll();
	}

	public Optional<CombinationOfTables> getCombination(String name) {
		return combinationRepository.findById(name);
	}

	public void createCombination(List<RestaurantTable> tables) {
		CombinationOfTables combination = new CombinationOfTables(tables);

		for (RestaurantTable table : tables) {
			Optional<RestaurantTable> foundTable =
			tableRepository.findById(table.getName());
			if (foundTable.isPresent()) {
				foundTable.get().addCombination(combination);
			} else {
				table.addCombination(combination);
				tableRepository.save(table);
			}
		}
		combinationRepository.save(combination);
	}

	public void deleteCombination(CombinationOfTables combination) {
		List<RestaurantTable> tables = combination.getRestaurantTables();
		for (RestaurantTable table : tables) {
			Optional<RestaurantTable> foundTable =
			tableRepository.findById(table.getName());
			foundTable.ifPresent(
			restaurantTable -> restaurantTable.removeCombination(
			combination));
		}
		combinationRepository.deleteById(combination.getName());
	}

	//	Largest RestaurantTable Size  --------------------------------------------------------

	public int getLargestTableSize() {
		return largestTableSize;
	}

	// Other
	public boolean willPartyFitOnTable(int partySize, List<RestaurantTable> tables) {
		int size = 0;
		for (RestaurantTable table : tables) {
			size += table.getSeats();
		}
		return size >= partySize;
	}
}
