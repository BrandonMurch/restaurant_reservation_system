/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class CombinationOfTables extends Sittable {

	@ManyToMany(targetEntity = RestaurantTable.class, cascade =
	CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "combination_table",
	joinColumns = @JoinColumn(name = "combination_id"),
	inverseJoinColumns = @JoinColumn(name = "table_id")
	)
	private final List<RestaurantTable> restaurantTables = new ArrayList<>();

	public CombinationOfTables() {
		super("", 0);
	}

	public CombinationOfTables(List<RestaurantTable> restaurantTables) {
		this();
		int seats = 0;
		for (RestaurantTable table : restaurantTables) {
			this.restaurantTables.add(table);
			seats += table.getSeats();
		}
		this.setName(calculateName());
		this.setSeats(seats);
	}

	private String calculateName() {
		return restaurantTables.stream().map(RestaurantTable::getName)
		.collect(Collectors.joining(", "));
	}

	public void setCustomTotalSeats(int seats) {
		this.setSeats(seats);
	}

	public List<RestaurantTable> getTables() {
		return this.restaurantTables;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CombinationOfTables that = (CombinationOfTables) o;
		return getSeats() == that.getSeats() &&
		Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSeats(), getName());
	}

	@Override
	public String toString() {
		return this.getName() + " - Total seats: "
		+ this.getSeats();
	}
}
