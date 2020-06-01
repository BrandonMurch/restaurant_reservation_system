package com.brandon.restaurant_reservation_system.restaurants.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationOfTables {
	private String name;
	private int totalSeats;
	private List<Table> tables;

	public CombinationOfTables() {
		totalSeats = 0;
		name = "";
		tables = new ArrayList<>();
	}

	public CombinationOfTables(Table table) {
		this();
		tables.add(table);
		calculateName();
	}

	public CombinationOfTables(List<Table> tables) {
		this();
		totalSeats = 0;
		for (Table table : tables) {
			this.tables.add(table);
			totalSeats += table.getSeats();
		}
		name = calculateName();
	}

	public String getName() {
		return this.name;
	}

	private String calculateName() {
		return tables.stream().map(Table::getName)
				.collect(Collectors.joining(", "));
	}

	public void addTable(Table table) {
		totalSeats += table.getSeats();
		name += ", " + table.getName();
	}

	public List<Table> getTables() {
		return this.tables;
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
