package com.brandon.restaurant_reservation_system.restaurants.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantTables {

	private List<Table> tableList;
	private List<CombinationOfTables> combinationsOfTablesList;
	private int largestTableSize;

	public RestaurantTables() {
		this.tableList = new ArrayList<>();
		this.combinationsOfTablesList = new ArrayList<>();
		this.largestTableSize = 0;
	}

	// INDIVIDUAL TABLES -------------------------------------------------------

	public List<Table> getAll() {
		return tableList;
	}

	public void setAll(List<Table> tableList) {
		this.tableList = tableList;
		updateLargestTableSize();
	}

	private void updateLargestTableSize() {
		for (Table table : tableList) {
			if (table.getSeats() > this.largestTableSize) {
				this.largestTableSize = table.getSeats();
			}
		}

		for (CombinationOfTables table : combinationsOfTablesList) {
			if (table.getTotalSeats() > this.largestTableSize) {
				this.largestTableSize = table.getTotalSeats();
			}
		}
	}

	public Optional<Table> get(String name) {
		for (Table table : tableList) {
			if (table.getName().equals(name)) {
				return Optional.of(table);
			}
		}
		return Optional.empty();
	}

	public void add(String name, int seats) {
		tableList.add(new Table(name, seats));
		updateLargestTableSize();
	}

	// TABLE COMBINATIONS ------------------------------------------------------

	public void remove(String name) {
		tableList.removeIf(
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

//	Largest Table Size  --------------------------------------------------------

	public void remove(
			CombinationOfTables combinationOfTables) {
		combinationsOfTablesList.remove(combinationOfTables);
		updateLargestTableSize();
	}

	public int getLargestTableSize() {
		return largestTableSize;
	}
}
