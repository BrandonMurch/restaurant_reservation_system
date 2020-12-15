/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name = "combination_of_tables")
@DiscriminatorValue("1")
public class CombinationOfTables extends RestaurantTable {

	@ManyToMany(targetEntity = SingleTable.class, cascade =
			CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "combination_table",
			joinColumns = @JoinColumn(name = "combination_id")
	)
	private final List<RestaurantTable> restaurantTables = new ArrayList<>();

	public CombinationOfTables() {
	}

	public CombinationOfTables(List<RestaurantTable> restaurantTables, int priority) {
		super();
		this.restaurantTables.addAll(restaurantTables);
		this.setSeats(calculateSeats(restaurantTables));
		this.setName(calculateName(restaurantTables));
		this.setPriority(priority);
	}

	public List<RestaurantTable> getAssociatedTables() {
		return this.restaurantTables;
	}

	public void removeAssociatedTables() {
		this.restaurantTables.clear();
	}

	private int calculateSeats(List<RestaurantTable> restaurantTables) {
		return restaurantTables.stream().reduce(0,
				(previous, current) -> previous + current.getSeats(), Integer::sum);
	}

	private String calculateName(List<RestaurantTable> restaurantTables) {
		return restaurantTables.stream().map(RestaurantTable::getName)
		.collect(Collectors.joining(", "));
	}

}
