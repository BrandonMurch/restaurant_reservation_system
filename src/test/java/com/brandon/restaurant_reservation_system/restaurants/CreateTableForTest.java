package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;

import java.util.Arrays;
import java.util.List;

public class CreateTableForTest {

	Restaurant restaurant;

	public CreateTableForTest(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public List<RestaurantTable> getTableList() {
		return Arrays.asList(
				getTable1(),
				getTable2(),
				getTable3(),
				getTable4(),
				getTable5());
	}

	public RestaurantTable getTable1() {
		return new RestaurantTable("21", 2);
	}

	public RestaurantTable getTable2() {
		return new RestaurantTable("22", 2);
	}

	public RestaurantTable getTable3() {
		return new RestaurantTable("23", 2);
	}

	public RestaurantTable getTable4() {
		return new RestaurantTable("5", 8);
	}

	public RestaurantTable getTable5() {
		return new RestaurantTable("k1", 2);
	}
}
