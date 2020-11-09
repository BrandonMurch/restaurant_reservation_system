package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;

import java.util.Arrays;
import java.util.List;

public class CreateCombinationsForTest {
	final List<RestaurantTable> restaurantTableList;

	public CreateCombinationsForTest(
	List<RestaurantTable> restaurantTableList) {
		this.restaurantTableList = restaurantTableList;
	}

	public List<CombinationOfTables> getCombinationList() {
		return Arrays.asList(
		getCombination1(),
		getCombination2(),
				getCombination3());
	}

	public CombinationOfTables getCombination1() {
		return new CombinationOfTables(
		Arrays.asList(
		this.restaurantTableList.get(0),
		this.restaurantTableList.get(1),
		this.restaurantTableList.get(2)
		), 1);
	}

	public CombinationOfTables getCombination2() {
		return new CombinationOfTables(
		Arrays.asList(
		this.restaurantTableList.get(0),
		this.restaurantTableList.get(1)
		), 1);
	}

	public CombinationOfTables getCombination3() {
		return new CombinationOfTables(
		Arrays.asList(
		this.restaurantTableList.get(1),
		this.restaurantTableList.get(2)
		), 1);
	}





}
