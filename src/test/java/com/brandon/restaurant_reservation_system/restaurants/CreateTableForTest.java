/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;

import java.util.Arrays;
import java.util.List;

public class CreateTableForTest {

	public CreateTableForTest(Restaurant restaurant) {
	}

	public static List<RestaurantTable> getTableList() {
		return Arrays.asList(
		getTable1(),
		getTable2(),
		getTable3(),
		getTable4(),
		getTable5());
	}

	public static RestaurantTable getTable1() {
        return new RestaurantTable("21", 2, 1);
    }

	public static RestaurantTable getTable2() {
        return new RestaurantTable("22", 2, 2);
    }

	public static RestaurantTable getTable3() {
        return new RestaurantTable("23", 2, 3);
    }

	public static RestaurantTable getTable4() {
        return new RestaurantTable("5", 8, 4);
    }

	public static RestaurantTable getTable5() {
        return new RestaurantTable("k1", 2, 5);
    }
}
