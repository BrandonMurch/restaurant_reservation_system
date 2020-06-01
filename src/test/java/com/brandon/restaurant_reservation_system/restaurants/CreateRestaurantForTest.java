package com.brandon.restaurant_reservation_system.restaurants;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.OpeningClosingPair;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.Table;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public  class CreateRestaurantForTest {

	private static final DateTimeFormatter timeFormat =
			GlobalVariables.getTimeFormat();

	public static Restaurant create() {
		String name = "The Restaurant";
		// new opening hours - get map from restaurant and set day of week
		RestaurantConfig config = createRestaurantConfigForTest();
		Restaurant restaurant = new Restaurant(name, config);
		CreateTableForTest createTable = new CreateTableForTest(restaurant);
		List<Table> tableList = createTable.getTableList();
		restaurant.setTableList(tableList);
		CreateCombinationsForTest createCombo =
				new CreateCombinationsForTest(tableList);
		restaurant.setTableCombinations(createCombo.getCombinationList());

		Map<DayOfWeek, Day> openingHours = restaurant.getOpeningHours();
		restaurant.setOpeningHours(getOpeningHours(openingHours));
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

		LocalTime opening = DateTimeHandler.parseTime("18:00", timeFormat);
		LocalTime closing = DateTimeHandler.parseTime("23:00", timeFormat);
		List<OpeningClosingPair> openingClosingPairs =
				Collections.singletonList(new OpeningClosingPair(opening,
						closing));


		Day day = new Day(DayOfWeek.SUNDAY, openingClosingPairs);
		day.allowBookingPerTimeInterval(15);
		openingHours.put(DayOfWeek.SUNDAY, day);
		return openingHours;
	}
}
