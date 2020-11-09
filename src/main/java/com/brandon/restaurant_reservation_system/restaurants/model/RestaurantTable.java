/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "restaurant_table")
public class RestaurantTable extends Sittable {

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "restaurantTables")
	@JsonIgnore
	private final Set<Booking> bookings = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "restaurantTables")
	@JsonIgnore
	private final Set<CombinationOfTables> combinations = new HashSet<>();

	public RestaurantTable(String name, int seats, int priority) {
		super(name, seats, priority);
	}

	public RestaurantTable() {
	}

	public void addBooking(Booking booking) {
		bookings.add(booking);
	}

	public void removeBooking(Booking booking) {
		bookings.remove(booking);
	}

	public void addCombination(CombinationOfTables combination) {
		combinations.add(combination);
	}

	public void removeCombination(CombinationOfTables combination) {
		combinations.remove(combination);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RestaurantTable that = (RestaurantTable) o;
		return getSeats() == that.getSeats() &&
		Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getSeats());
	}

	@Override
	public String toString() {
		return "RestaurantTable{" +
		"name='" + this.getName() + '\'' +
		", seats=" + this.getSeats() +
		'}';
	}
}
