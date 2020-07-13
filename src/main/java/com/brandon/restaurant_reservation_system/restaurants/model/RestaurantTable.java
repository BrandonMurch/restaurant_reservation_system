package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RestaurantTable {

	@Id
	private String name;
	private int seats;
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "restaurantTables")
	@JsonIgnore
	private final Set<Booking> bookings;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "restaurantTables")
	@JsonIgnore
	private final Set<CombinationOfTables> combinations;


	public RestaurantTable(String name, int seats) {
		this();
		this.name = name;
		this.seats = seats;
	}

	public RestaurantTable() {
		bookings = new HashSet<>();
		combinations = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public int getSeats() {
		return seats;
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
	public String toString() {
		return "RestaurantTable{" +
				"name='" + name + '\'' +
				", seats=" + seats +
				'}';
	}
}
