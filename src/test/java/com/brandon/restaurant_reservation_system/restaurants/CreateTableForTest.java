package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.Table;

import java.util.ArrayList;
import java.util.List;

public class CreateTableForTest {

	Restaurant restaurant;

	public CreateTableForTest(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	public Table getTable1() {
		return new Table("21", 2, true,
		restaurant);
	}
	public Table getTable2() {
		return new Table("22", 2, true,
				restaurant);
	}
	public Table getTable3() {
		return new Table("23", 2, true,
				restaurant);
	}
	public Table getTable4() {
		return new Table("5", 8, false,
				restaurant);
	}
	public Table getTable5() {
		return new Table("k1", 2, false,
				restaurant);
	}
	public List<Table> getTableList() {
		List<Table> tableList = new ArrayList<>();
		tableList.add(getTable1());
		tableList.add(getTable2());
		tableList.add(getTable3());
		tableList.add(getTable4());
		tableList.add(getTable5());
		return tableList;
	}
}
