package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public  class CreateRestaurantForTest {

	private static final DateTimeFormatter timeFormat =
			GlobalVariables.getTimeFormat();

	public static Restaurant create() {
		String name = "The Restaurant";
		// new opening hours - get map from restaurant and set day of week
		RestaurantConfig config = createRestaurantConfigForTest();
		Restaurant restaurant = new Restaurant(name, config);
		CreateTableForTest createTable = new CreateTableForTest(restaurant);
		List<RestaurantTable> restaurantTableList = createTable.getTableList();
		restaurant.setTableList(restaurantTableList);
		CreateCombinationsForTest createCombo =
				new CreateCombinationsForTest(restaurantTableList);
		restaurant.setTableCombinations(createCombo.getCombinationList());

		Map<DayOfWeek, Day> openingHours = restaurant.getOpeningHours();
		restaurant.setOpeningHours(getOpeningHours(openingHours));
		restaurant.allowBookingPerTimeInterval(15);
		return restaurant;
	}

	private static RestaurantConfig createRestaurantConfigForTest() {
		RestaurantConfig config = new RestaurantConfig();
		config.setCapacity(10);
		config.setCanABookingOccupyALargerTable(true);
		config.setStandardBookingDuration(120);

		return config;

	}

	private static Map<DayOfWeek, Day> getOpeningHours(Map<DayOfWeek, Day> openingHours) {

		Map<DayOfWeek, Day> newMap = new HashMap<>(openingHours);

		DayOfWeek[] days = {
				DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY,
				DayOfWeek.SATURDAY,
				DayOfWeek.SUNDAY
		};

		for (DayOfWeek day : days) {
			LocalTime opening = LocalTime.of(18, 0);
			LocalTime closing = LocalTime.of(23, 20);
			newMap.computeIfPresent(day, (key, val) -> {
				val.setOpen(true);
				val.addOpeningAndClosing(opening, closing);
				return val;
			});
		}
		return newMap;
	}
}
