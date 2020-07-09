package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantStub {

	public static void populateRestaurant(Restaurant restaurant) {
		RestaurantConfig config = new RestaurantConfig();
		config.setCapacity(20);
		DateRange range = getDateRange();
		config.setStandardBookingDuration(120);
		config.setCanABookingOccupyALargerTable(false);
		config.setPeoplePerInterval(0);

		restaurant.setConfig(config);
		restaurant.setTableList(getTables());
		restaurant.setTableCombinations(getCombinations());
		restaurant.setBookingDateRange(range.getStart(),
				range.getEnd());
		restaurant.setOpeningHours(
				modifyOpeningHours(restaurant.getOpeningHours()));
		restaurant.allowBookingPerTimeInterval(15);
	}

	private static DateRange getDateRange() {
		LocalDate start = LocalDate.now();
		LocalDate end = LocalDate.of(2020, 8, 31);

		return new DateRange(start, end);
	}

	private static List<RestaurantTable> getTables() {
		return Arrays.asList(
				new RestaurantTable("k1", 2),
				new RestaurantTable("k2", 2),
				new RestaurantTable("b1", 2),
				new RestaurantTable("b2", 2),
				new RestaurantTable("1", 4),
				new RestaurantTable("5", 4),
				new RestaurantTable("20", 2),
				new RestaurantTable("21", 2),
				new RestaurantTable("22", 2),
				new RestaurantTable("23", 2),
				new RestaurantTable("24", 2),
				new RestaurantTable("25", 2)
		);
	}

	private static List<CombinationOfTables> getCombinations() {
		RestaurantTable restaurantTable1 = new RestaurantTable("20", 2);
		RestaurantTable restaurantTable2 = new RestaurantTable("21", 2);
		RestaurantTable restaurantTable3 = new RestaurantTable("22", 2);
		RestaurantTable restaurantTable4 = new RestaurantTable("1", 2);
		RestaurantTable restaurantTable5 = new RestaurantTable("5", 2);

		return Arrays.asList(
				new CombinationOfTables(Arrays.asList(
						restaurantTable1, restaurantTable2
				)),
				new CombinationOfTables(Arrays.asList(
						restaurantTable1, restaurantTable2, restaurantTable3
				)),
				new CombinationOfTables(Arrays.asList(
						restaurantTable2, restaurantTable3
				)),
				new CombinationOfTables(Arrays.asList(
						restaurantTable4, restaurantTable5
				))
		);
	}

	private List<LocalTime> getBookingTimes() {
		return Arrays.asList(LocalTime.of(18, 0),
				LocalTime.of(20, 30),
				LocalTime.of(23, 15));
	}

	private static Map<DayOfWeek, Day> modifyOpeningHours(
			Map<DayOfWeek, Day> map) {
		Map<DayOfWeek, Day> newMap = new HashMap<>(map);

		DayOfWeek[] days = {
				DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY,
				DayOfWeek.SATURDAY
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
