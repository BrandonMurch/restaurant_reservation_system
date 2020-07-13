package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.CombinationRepository;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private void updateLargestTableSize() {
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
}
