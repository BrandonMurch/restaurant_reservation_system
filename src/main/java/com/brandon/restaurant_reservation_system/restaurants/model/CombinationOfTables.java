/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity(name = "combination_of_tables")
public class CombinationOfTables extends Sittable {

	@ManyToMany(targetEntity = RestaurantTable.class, cascade =
	CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "combination_table",
	joinColumns = @JoinColumn(name = "combination_id"),
	inverseJoinColumns = @JoinColumn(name = "table_id")
	)
	private final List<RestaurantTable> restaurantTables = new ArrayList<>();

	public CombinationOfTables() {
	}

	public CombinationOfTables(List<RestaurantTable> restaurantTables, int priority) {
		super();
		this.setSeats(calculateSeats(restaurantTables));
		this.setName(calculateName(restaurantTables));
		this.setPriority(priority);
	}

	public void setCustomTotalSeats(int seats) {
		this.setSeats(seats);
	}

	public List<RestaurantTable> getTables() {
		return this.restaurantTables;
	}

	private int calculateSeats(List<RestaurantTable> restaurantTables) {
		return restaurantTables.stream().reduce(0,
		(previous, current) -> previous + current.getSeats(), Integer::sum);
	}

	private String calculateName(List<RestaurantTable> restaurantTables) {
		return restaurantTables.stream().map(RestaurantTable::getName)
		.collect(Collectors.joining(", "));
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
