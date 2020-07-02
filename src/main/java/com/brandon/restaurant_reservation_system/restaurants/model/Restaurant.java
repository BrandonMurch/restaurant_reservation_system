package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantCache;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Restaurant {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private BookingTimes bookingTimes;
	private BookingDateRange bookingDateRange;
	private List<Table> tableList;
	private List<CombinationOfTables> combinationsOfTablesList;
	private RestaurantConfig config;
	private RestaurantCache cache;

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
		this.tableList = new ArrayList<>();
		this.config = restaurantConfig;
		bookingDateRange = new BookingDateRange(120);
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig,
	                  List<LocalTime> bookingTimes) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(bookingTimes);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return config.getCapacity();
	}

	public void setCapacity(int capacity) {
		config.setCapacity(capacity);
	}

	public List<Table> getTableList() {
		return tableList;
	}

	public void setTableList(List<Table> tableList) {this.tableList = tableList;}

	public Table getTable(int i) {
		return tableList.get(i);
	}

	public void addTable(String name, int seats, boolean isJoinable){
		tableList.add(new Table(name, seats, isJoinable, this));
	}

	public void removeTable(String name) {
		tableList.removeIf(
				restaurantTable -> restaurantTable.getName().equals(name));
	}

	public List<CombinationOfTables> getCombinationsOfTables() {
		return combinationsOfTablesList;
	}

	public void addTableCombination(CombinationOfTables combinationOfTables){
		combinationsOfTablesList.add(combinationOfTables);
	}

	public void removeTableCombination(
			CombinationOfTables combinationOfTables) {
		combinationsOfTablesList.remove(combinationOfTables);
	}

	public void setTableCombinations(
			List<CombinationOfTables> combinationsOfTablesList) {
		this.combinationsOfTablesList = combinationsOfTablesList;
	}

	// TODO: Split this up, so that you don't need to get config, then a prop
	public RestaurantConfig getRestaurantConfig() {
		return config;
	}

	public void setRestaurantConfig(
			RestaurantConfig restaurantConfig) {
		this.config = restaurantConfig;
	}

	public boolean canABookingOccupyALargerTable() {
		return config.canABookingOccupyALargerTable();
	}

	public Duration getStandardBookingDuration() {
		return config.getStandardBookingDuration();
	}

	public Set<LocalDate> getFullDates() {
		return cache.getFullDates();
	}

	public boolean isDateFull(LocalDate date) {
		return cache.isDateFull(date);
	}

	public List<LocalTime> getBookingTimes() {
		return getBookingTimes(LocalDate.now());
	}

	public List<LocalTime> getBookingTimes(LocalDate date) {
		return bookingTimes.getBookingTimes(date);
	}

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
