package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Restaurant {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private Map<DayOfWeek, Day> openingHours;
	private List<Table> tableList;
	private List<CombinationOfTables> combinationsOfTablesList;
	// todo hashSet for dates, if they are in the set, they are full.
	private RestaurantConfig restaurantConfig;

	public Restaurant() {
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig) {
		this.name = name;
		initOpeningHours();
		this.tableList = new ArrayList<>();
		this.restaurantConfig = restaurantConfig;
	}

	private void initOpeningHours() {
		this.openingHours = new HashMap<>();
		for (DayOfWeek day : DayOfWeek.values()) {
			this.openingHours.put(day, new Day(day, false));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<DayOfWeek, Day> getOpeningHours() {
		return openingHours;
	}

	public Day getOpeningHours(DayOfWeek dayOfWeek) {
		return openingHours.get(dayOfWeek);
	}


	public void setOpeningHours(
			Map<DayOfWeek, Day> openingHours) {
		this.openingHours = openingHours;
	}

	public int getCapacity() {
		return restaurantConfig.getCapacity();
	}

	public void setCapacity(int capacity) {
		restaurantConfig.setCapacity(capacity);
	}

	public List<Table> getTableList() {
		return tableList;
	}

	public void setTableList(List<Table> tableList) {this.tableList = tableList;}

	public Table getTable(int i) {
		return tableList.get(i);
	}

	public void addTable(String name, int seats, boolean isJoinable){
		tableList.add(new Table(name, seats, isJoinable, this));
	}

	public void removeTable(String name) {
		tableList.removeIf(
				restaurantTable -> restaurantTable.getName().equals(name));
	}

	public List<CombinationOfTables> getCombinationsOfTables() {
		return combinationsOfTablesList;
	}

	public void addTableCombination(CombinationOfTables combinationOfTables){
		combinationsOfTablesList.add(combinationOfTables);
	}

	public void removeTableCombination(CombinationOfTables combinationOfTables) {
		combinationsOfTablesList.remove (combinationOfTables);
	}

	public void setTableCombinations(List<CombinationOfTables> combinationsOfTablesList) {
		this.combinationsOfTablesList = combinationsOfTablesList;
	}

	public RestaurantConfig getRestaurantConfig() {
		return restaurantConfig;
	}

	public void setRestaurantConfig(
			RestaurantConfig restaurantConfig) {
		this.restaurantConfig = restaurantConfig;
	}

	public boolean canABookingOccupyALargerTable() {
		return restaurantConfig.canABookingOccupyALargerTable();
	}

	public Duration getStandardBookingDuration() {
		return restaurantConfig.getStandardBookingDuration();
	}


}
