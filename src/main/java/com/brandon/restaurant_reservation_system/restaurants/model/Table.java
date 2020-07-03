package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Table {

	@Id
	@GeneratedValue
	private long id;
	private String name;
	private int seats;

	public Table() {
	}

	public Table(String name, int seats) {
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
		return "Table{" +
				"name='" + name + '\'' +
				", seats=" + seats +
				'}';
	}
}
