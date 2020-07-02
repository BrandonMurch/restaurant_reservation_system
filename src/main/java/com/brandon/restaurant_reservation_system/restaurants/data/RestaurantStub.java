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

	private final Restaurant restaurant;
	private final RestaurantConfig config;

	public RestaurantStub() {
		config = new RestaurantConfig();
		config.setCapacity(20);
		DateRange range = getDateRange();
		config.setStandardBookingDuration(120);
		config.setCanABookingOccupyALargerTable(false);
		config.setPeoplePerInterval(0);

		restaurant = new Restaurant("Restaurant", config);
		restaurant.setTableList(getTables());
		restaurant.setTableCombinations(getCombinations());
		restaurant.setBookingDateRange(range.getReservationStartDate(),
				range.getReservationEndDate());
		// TODO: this needs to be set in BookingTimes
//		restaurant.setOpeningHours(
//				setOpeningHours(restaurant.getOpeningHours()));
	}

	private DateRange getDateRange() {
		LocalDate start = LocalDate.now();
		LocalDate end = LocalDate.of(2020, 8, 31);

		return new DateRange(start, end);
	}

	private List<Table> getTables() {
		return Arrays.asList(
				new Table("k1", 2, false, restaurant),
				new Table("k2", 2, false, restaurant),
				new Table("b1", 2, false, restaurant),
				new Table("b2", 2, false, restaurant),
				new Table("1", 4, false, restaurant),
				new Table("5", 4, false, restaurant),
				new Table("20", 2, false, restaurant),
				new Table("21", 2, false, restaurant),
				new Table("22", 2, false, restaurant),
				new Table("23", 2, false, restaurant),
				new Table("24", 2, false, restaurant),
				new Table("25", 2, false, restaurant)
		);
	}

	private List<CombinationOfTables> getCombinations() {
		Table table1 = new Table("20", 2, false, restaurant);
		Table table2 = new Table("21", 2, false, restaurant);
		Table table3 = new Table("22", 2, false, restaurant);
		Table table4 = new Table("1", 2, false, restaurant);
		Table table5 = new Table("5", 2, false, restaurant);

		return Arrays.asList(
				new CombinationOfTables(Arrays.asList(
						table1, table2
				)),
				new CombinationOfTables(Arrays.asList(
						table1, table2, table3
				)),
				new CombinationOfTables(Arrays.asList(
						table2, table3
				)),
				new CombinationOfTables(Arrays.asList(
						table4, table5
				))
		);
	}

	private List<LocalTime> getBookingTimes() {
		return Arrays.asList(LocalTime.of(18, 0),
				LocalTime.of(20, 30),
				LocalTime.of(23, 15));
	}

	private Map<DayOfWeek, Day> setOpeningHours(Map<DayOfWeek, Day> map) {
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

	public Restaurant getRestaurant() {
		return restaurant;
	}


}
