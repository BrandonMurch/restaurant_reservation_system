package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class CombinationOfTables {

	@ManyToMany(targetEntity = RestaurantTable.class, cascade =
			CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "combination_table",
			joinColumns = @JoinColumn(name = "combination_id"),
			inverseJoinColumns = @JoinColumn(name = "table_id"))
	private final List<RestaurantTable> restaurantTables;
	private int totalSeats;
	@Id
	private String name;

	public CombinationOfTables() {
		totalSeats = 0;
		name = "";
		restaurantTables = new ArrayList<>();
	}

	public CombinationOfTables(List<RestaurantTable> restaurantTables) {
		this();
		totalSeats = 0;
		for (RestaurantTable table : restaurantTables) {
			this.restaurantTables.add(table);
			totalSeats += table.getSeats();
		}
		name = calculateName();
	}

	private String calculateName() {
		return restaurantTables.stream().map(RestaurantTable::getName)
				.collect(Collectors.joining(", "));
	}

	public String getName() {
		return this.name;
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
