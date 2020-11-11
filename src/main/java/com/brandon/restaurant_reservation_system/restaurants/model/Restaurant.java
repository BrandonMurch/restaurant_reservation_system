/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.BookingDateRange;
import com.brandon.restaurant_reservation_system.restaurants.data.BookingTimes;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantCache;
import com.brandon.restaurant_reservation_system.restaurants.data.RestaurantConfig;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.RestaurantConfigurationException;
import com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

@Component
public class Restaurant implements Serializable {

	private static final long serialVersionUID = 2993992281945949085L;

	@Autowired
	private transient RestaurantCache cache;
	@Autowired
	private transient TableHandlerService tables;
	private String name;
	private BookingTimes bookingTimes = new BookingTimes();
	private transient BookingDateRange bookingDateRange = new BookingDateRange(0);
	private RestaurantConfig config;

	public Restaurant() {
	}

	public Restaurant(String name,
					  RestaurantConfig restaurantConfig,
					  int minutesBetweenBookingSlots) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(minutesBetweenBookingSlots);
		serialize();
	}

	private Restaurant(String name,
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
		serialize();
	}

	@PostConstruct
	private void postConstruct() {
		// TODO: remove this when in production
		//		boolean isDeserializeSuccess = deserialize();

		//		if (!isDeserializeSuccess) {
		PopulateRestaurantService.populateRestaurant(this);
		//		}
		// TODO: Remove this when database is created.
		PopulateRestaurantService.populateRestaurantTables(this);
	}

	// Name --------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		serialize();
	}

	// Capacity ----------------------------------------------------------------

	public int getCapacity() {
		return config.getCapacity();
	}

	public void setCapacity(int capacity) {
		config.setCapacity(capacity);
		serialize();
	}

	// Tables ------------------------------------------------------------------

	public List<RestaurantTable> getTableList() {
		return tables.getAll();
	}

	public void setTableList(List<RestaurantTable> restaurantTableList) {
		tables.addAll(restaurantTableList);
	}

	public Optional<RestaurantTable> getTable(String name) {
		return tables.get(name);
	}

	public void addTable(String name, int seats) {
		tables.add(name, seats);
	}

	public void addTable(RestaurantTable table) {
		tables.add(table);
	}

	public void updateTable(RestaurantTable existing, RestaurantTable updated) {
		tables.update(existing, updated);
	}

	public void updateAllTables(List<RestaurantTable> tables) {
		this.tables.updateAll(tables);
	}

	public int removeTable(String name) {
		return tables.remove(name);
	}


	public boolean hasCombinationsOfTables() {
		List<CombinationOfTables> combinations = getAllCombinationsOfTables();
		return combinations != null && !combinations.isEmpty();
	}

	public List<CombinationOfTables> getAllCombinationsOfTables() {
		return tables.getAllCombinations();
	}

	public Optional<CombinationOfTables> getCombinationOfTables(String name) {
		return tables.getCombination(name);
	}

	public void addTableCombination(List<RestaurantTable> tables) {
		this.tables.createCombination(tables);
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

	public List<LocalTime> getBookingTimes() {
		return getBookingTimes(LocalDate.now());
	}

	public List<LocalTime> getBookingTimes(LocalDate date) {
		return bookingTimes.getBookingTimes(date);
	}

	public boolean isBookingTime(LocalDateTime dateTime) {
		if (bookingTimes == null) {
			throw new RestaurantConfigurationException("booking times");
		}
		return bookingTimes.isBookingTime(dateTime);
	}

	public void setConfig(RestaurantConfig config) {
		this.config = config;
	}

	public void setOpeningHours(Map<DayOfWeek, Day> openingHours) {
		bookingTimes.setOpeningHours(openingHours);
		serialize();
	}

	public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
		bookingTimes.allowBookingPerTimeInterval(bookingIntervalInMinutes);
		serialize();
	}


	// Date Range    -----------------------------------------------------------


	public DateRange getBookingDateRange() {
		return bookingDateRange.getBookingRange();
	}

	public void setBookingDateRange(int bookingHorizonInDays) {
		bookingDateRange.setBookingRange(bookingHorizonInDays);
		serialize();
	}

	public void setBookingDateRange(LocalDate start, LocalDate end) {
		bookingDateRange = new BookingDateRange(new DateRange(start, end));
		serialize();
	}

	public void allowBookingsOnlyAtCertainTimes(List<LocalTime> times) {
		bookingTimes.allowBookingsOnlyAtCertainTimes(times);
		serialize();
	}

	// Serialization & Deserialization ----------------------------------------


	private void serialize() {

		try {
			FileOutputStream fileOut = new FileOutputStream("restaurant.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean deserialize() {
		try {
			FileInputStream fileIn = new FileInputStream("restaurant.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Restaurant restaurant = (Restaurant) in.readObject();

			this.name = restaurant.name;
			this.bookingTimes = restaurant.bookingTimes;
			this.bookingDateRange = restaurant.bookingDateRange;
			this.config = restaurant.config;
			in.close();
			fileIn.close();
			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}

	// Cache -------------------------------------------------------------------

	public SortedSet<LocalDate> getAvailableDates() {
		return cache.getAvailableDates();
	}

	public void addAvailableDate(LocalDate date) {
		cache.addAvailableDate(date);
	}

	public void removeDateIfUnavailable(LocalDate date) {
		cache.removeDateIfUnavailable(date);
	}

	public Map<LocalDate, Integer> getBookingsPerDate() {
		return cache.getBookingsPerDate();
	}

	public void addBookingToDate(LocalDate date, Integer numberOfBookings) {
		cache.addBookingToDate(date, numberOfBookings);
	}

	public void removeBookingFromDate(LocalDate date, Integer numberOfBookings) {
		cache.removeBookingFromDate(date, numberOfBookings);
	}

}
