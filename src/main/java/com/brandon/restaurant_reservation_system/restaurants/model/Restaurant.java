package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingTimes;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Restaurant {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private BookingTimes bookingTimes;
	private BookingDateRange bookingDateRange;
	private RestaurantTables tables;
	private RestaurantConfig config;

	public Restaurant() {
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig,
	                  int minutesBetweenBookingSlots) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(minutesBetweenBookingSlots);
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig) {
		this.name = name;
		this.config = restaurantConfig;
		bookingDateRange = new BookingDateRange(120);
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig,
	                  List<LocalTime> bookingTimes) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(bookingTimes);
	}

	// Name --------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Capacity ----------------------------------------------------------------

	public int getCapacity() {
		return config.getCapacity();
	}

	public void setCapacity(int capacity) {
		config.setCapacity(capacity);
	}

	// Tables ------------------------------------------------------------------


	public List<Table> getTableList() {
		return tables.getAll();
	}

	public void setTableList(List<Table> tableList) {
		tables.setAll(tableList);
	}

	public Optional<Table> getTable(String name) {
		return tables.get(name);
	}

	public void addTable(String name, int seats) {
		tables.add(name, seats);
	}

	public void removeTable(String name) {
		tables.remove(name);
	}

	public void addTableCombination(CombinationOfTables combinationOfTables) {
		tables.add(combinationOfTables);
	}

	public void removeTableCombination(
			CombinationOfTables combinationOfTables) {
		tables.remove(combinationOfTables);
	}

	public void setTableCombinations(
			List<CombinationOfTables> combinationsOfTablesList) {
		tables.setAllCombinations(combinationsOfTablesList);
	}

	public boolean hasCombinationsOfTables() {
		List<CombinationOfTables> combinations = getCombinationsOfTables();
		return combinations != null && !combinations.isEmpty();
	}

	public List<CombinationOfTables> getCombinationsOfTables() {
		return tables.getAllCombinations();
	}

	public int getLargestTableSize() {
		return tables.getLargestTableSize();
	}

	// Config ------------------------------------------------------------------

	public boolean canABookingOccupyALargerTable() {
		return config.canABookingOccupyALargerTable();
	}

	public Duration getStandardBookingDuration() {
		return config.getStandardBookingDuration();
	}

	// Booking times -----------------------------------------------------------

	public boolean isOpenOnDate(LocalDate date) {
		return bookingTimes.isOpenOnDate(date);
	}

	public Map<DayOfWeek, Day> getOpeningHours() {
		return bookingTimes.getOpeningHours();
	}

	public void setOpeningHours(Map<DayOfWeek, Day> openingHours) {
		bookingTimes.setOpeningHours(openingHours);
	}

	public List<LocalTime> getBookingTimes() {
		return getBookingTimes(LocalDate.now());
	}

	public List<LocalTime> getBookingTimes(LocalDate date) {
		return bookingTimes.getBookingTimes(date);
	}

	public boolean isBookingTime(LocalDateTime dateTime) {
		return bookingTimes.isBookingTime(dateTime);
	}

	public void allowBookingsOnlyAtCertainTimes(List<LocalTime> times) {
		bookingTimes.allowBookingsOnlyAtCertainTimes(times);
	}

	public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
		bookingTimes.allowBookingPerTimeInterval(bookingIntervalInMinutes);
	}


	// Date Range    -----------------------------------------------------------


	public DateRange getBookingDateRange() {
		return bookingDateRange.getBookingRange();
	}

	public void setBookingDateRange(int bookingHorizonInDays) {
		bookingDateRange.setBookingRange(bookingHorizonInDays);
	}

	public void setBookingDateRange(LocalDate start, LocalDate end) {
		bookingDateRange.setBookingRange(new DateRange(start, end));
	}

}
