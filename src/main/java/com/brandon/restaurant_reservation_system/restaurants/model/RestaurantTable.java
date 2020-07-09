package com.brandon.restaurant_reservation_system.restaurants.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class RestaurantTable implements Serializable {

	private static final long serialVersionUID = -7171246555952936342L;
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private int seats;
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<RestaurantTable> tables;

	public RestaurantTable() {
	}

	public RestaurantTable(String name, int seats) {
		this.name = name;
		this.seats = seats;
	}

	public String getName() {
		return name;
	}

	public int getSeats() {
		return seats;
	}

	@Override
	public String toString() {
		return "RestaurantTable{" +
				"name='" + name + '\'' +
				", seats=" + seats +
				'}';
	}
}
