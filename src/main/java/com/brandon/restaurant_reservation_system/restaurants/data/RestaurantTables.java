package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantTables implements Serializable {

	private static final long serialVersionUID = -431126262467819043L;
	private List<RestaurantTable> restaurantTableList = new ArrayList<>();
	private List<CombinationOfTables> combinationsOfTablesList = new ArrayList<>();
	private int largestTableSize = 0;

	public RestaurantTables() {
	}

	// INDIVIDUAL TABLES -------------------------------------------------------

	public List<RestaurantTable> getAll() {
		return restaurantTableList;
	}

	public void setAll(List<RestaurantTable> restaurantTableList) {
		this.restaurantTableList = restaurantTableList;
		updateLargestTableSize();
	}

	private void updateLargestTableSize() {
		for (RestaurantTable restaurantTable : restaurantTableList) {
			if (restaurantTable.getSeats() > this.largestTableSize) {
				this.largestTableSize = restaurantTable.getSeats();
			}
		}

		for (CombinationOfTables table : combinationsOfTablesList) {
			if (table.getTotalSeats() > this.largestTableSize) {
				this.largestTableSize = table.getTotalSeats();
			}
		}
	}

	public Optional<RestaurantTable> get(String name) {
		for (RestaurantTable restaurantTable : restaurantTableList) {
			if (restaurantTable.getName().equals(name)) {
				return Optional.of(restaurantTable);
			}
		}
		return Optional.empty();
	}

	public void add(String name, int seats) {
		restaurantTableList.add(new RestaurantTable(name, seats));
		updateLargestTableSize();
	}

	// TABLE COMBINATIONS ------------------------------------------------------

	public void remove(String name) {
		restaurantTableList.removeIf(
				restaurantTable -> restaurantTable.getName().equals(name)
		);
		updateLargestTableSize();
	}

	public List<CombinationOfTables> getAllCombinations() {
		return combinationsOfTablesList;
	}

	public void setAllCombinations(
			List<CombinationOfTables> combinationsOfTablesList) {
		this.combinationsOfTablesList = combinationsOfTablesList;
		updateLargestTableSize();
	}

	public void add(CombinationOfTables combinationOfTables) {
		combinationsOfTablesList.add(combinationOfTables);
		updateLargestTableSize();
	}

//	Largest RestaurantTable Size  --------------------------------------------------------

	public void remove(
			CombinationOfTables combinationOfTables) {
		combinationsOfTablesList.remove(combinationOfTables);
		updateLargestTableSize();
	}

	public int getLargestTableSize() {
		return largestTableSize;
	}
}
