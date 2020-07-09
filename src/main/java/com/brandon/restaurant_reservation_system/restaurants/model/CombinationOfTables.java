package com.brandon.restaurant_reservation_system.restaurants.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationOfTables implements Serializable {

	private static final long serialVersionUID = 6133316649925582023L;
	private String name;
	private int totalSeats;
	private final List<RestaurantTable> restaurantTables;

	public CombinationOfTables(RestaurantTable restaurantTable) {
		this();
		restaurantTables.add(restaurantTable);
		calculateName();
	}

	public CombinationOfTables() {
		totalSeats = 0;
		name = "";
		restaurantTables = new ArrayList<>();
	}

	private String calculateName() {
		return restaurantTables.stream().map(RestaurantTable::getName)
				.collect(Collectors.joining(", "));
	}

	public String getName() {
		return this.name;
	}

	public CombinationOfTables(List<RestaurantTable> restaurantTables) {
		this();
		totalSeats = 0;
		for (RestaurantTable restaurantTable : restaurantTables) {
			this.restaurantTables.add(restaurantTable);
			totalSeats += restaurantTable.getSeats();
		}
		name = calculateName();
	}

	public void addTable(RestaurantTable restaurantTable) {
		totalSeats += restaurantTable.getSeats();
		name += ", " + restaurantTable.getName();
	}

	public List<RestaurantTable> getRestaurantTables() {
		return this.restaurantTables;
	}

	public int getTotalSeats() {
		return this.totalSeats;
	}

	public void setCustomTotalSeats(int seats) {
		this.totalSeats = seats;
	}

	@Override
	public String toString() {
		return this.name + " - Total seats: "
				+ this.totalSeats;
	}
}
