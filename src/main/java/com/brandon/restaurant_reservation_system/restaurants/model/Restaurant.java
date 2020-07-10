package com.brandon.restaurant_reservation_system.restaurants.model;

import com.brandon.restaurant_reservation_system.restaurants.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

@Component
public class Restaurant implements Serializable {

	private static final long serialVersionUID = 2993992281945949085L;
	private String name;
	private BookingTimes bookingTimes;
	private BookingDateRange bookingDateRange;
	private RestaurantTables tables;
	private RestaurantConfig config;
	@Autowired
	private transient RestaurantCache cache;

	public Restaurant() {
		deserialize();
		if (name == null) {
			RestaurantStub.populateRestaurant(this);
		}
	}

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig,
	                  int minutesBetweenBookingSlots) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(minutesBetweenBookingSlots);
		serialize();
	}

	// TODO: set constructor for pre-set booking times.

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig) {
		this.name = name;
		this.config = restaurantConfig;
		bookingDateRange = new BookingDateRange(120);
		tables = new RestaurantTables();
		bookingTimes = new BookingTimes();
		serialize();
	}

	// Name --------------------------------------------------------------------

	public Restaurant(String name,
	                  RestaurantConfig restaurantConfig,
	                  List<LocalTime> bookingTimes) {
		this(name, restaurantConfig);
		this.bookingTimes = new BookingTimes(bookingTimes);
		serialize();
	}

	// Capacity ----------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		serialize();
	}

	// Tables ------------------------------------------------------------------

	public int getCapacity() {
		return config.getCapacity();
	}

	public void setCapacity(int capacity) {
		config.setCapacity(capacity);
		serialize();
	}

	public List<RestaurantTable> getTableList() {
		return tables.getAll();
	}

	public void setTableList(List<RestaurantTable> restaurantTableList) {
		tables.setAll(restaurantTableList);
		serialize();
	}

	public Optional<RestaurantTable> getTable(String name) {
		return tables.get(name);
	}

	public void addTable(String name, int seats) {
		tables.add(name, seats);
		serialize();
	}

	public void removeTable(String name) {
		tables.remove(name);
		serialize();
	}

	public void addTableCombination(CombinationOfTables combinationOfTables) {
		tables.add(combinationOfTables);
		serialize();
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

	public void removeTableCombination(
			CombinationOfTables combinationOfTables) {
		tables.remove(combinationOfTables);
		serialize();
	}

	// Booking times -----------------------------------------------------------

	public boolean isOpenOnDate(LocalDate date) {
		return bookingTimes.isOpenOnDate(date);
	}

	public Map<DayOfWeek, Day> getOpeningHours() {
		return bookingTimes.getOpeningHours();
	}

	public void setTableCombinations(
			List<CombinationOfTables> combinationsOfTablesList) {
		tables.setAllCombinations(combinationsOfTablesList);
		serialize();
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

	public void serialize() {

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


	// Date Range    -----------------------------------------------------------


	public DateRange getBookingDateRange() {
		return bookingDateRange.getBookingRange();
	}

	public void setBookingDateRange(int bookingHorizonInDays) {
		bookingDateRange.setBookingRange(bookingHorizonInDays);
		serialize();
	}

	public void allowBookingsOnlyAtCertainTimes(List<LocalTime> times) {
		bookingTimes.allowBookingsOnlyAtCertainTimes(times);
		serialize();
	}

	// Serialization & Deserialization ----------------------------------------

	public void setBookingDateRange(LocalDate start, LocalDate end) {
		bookingDateRange.setBookingRange(new DateRange(start, end));
		serialize();
	}

	public void deserialize() {
		try {
			FileInputStream fileIn = new FileInputStream("restaurant.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Restaurant restaurant = (Restaurant) in.readObject();

			this.name = restaurant.name;
			this.bookingTimes = restaurant.bookingTimes;
			this.bookingDateRange = restaurant.bookingDateRange;
			this.tables = restaurant.tables;
			this.config = restaurant.config;
			in.close();
			fileIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	// Cache -------------------------------------------------------------------

	public SortedSet<LocalDate> getAvailableDates() {
		return cache.getAvailableDates();
	}

}
