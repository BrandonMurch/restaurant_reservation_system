package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Table {

	@Id
	@GeneratedValue
	private long id;
	private Restaurant restaurant;
	private String name;
	private int seats;
	private boolean isJoinable;

	public Table() {}

	public Table(String name, int seats, boolean isJoinable,
	             Restaurant restaurant) {
		this.name = name;
		this.seats = seats;
		this.isJoinable = isJoinable;
		this.restaurant = restaurant;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public boolean isJoinable() {
		return isJoinable;
	}

	public void setJoinable(boolean joinable) {
		isJoinable = joinable;
	}

	@Override
	public String toString() {
		return "Table{" +
				"name='" + name + '\'' +
				", seats=" + seats +
				'}';
	}
}
