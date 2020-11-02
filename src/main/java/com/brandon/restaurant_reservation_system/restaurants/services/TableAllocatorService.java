/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class TableAllocatorService {

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private Restaurant restaurant;
	private List<RestaurantTable> restaurantTableList;
	private Map<Integer, RestaurantTable> availableTables;
	private Map<Integer, CombinationOfTables> availableCombinations;

	public TableAllocatorService() {
	}

	public TableAllocatorService(Restaurant restaurant) {
		restaurantTableList = restaurant.getTableList();
		this.restaurant = restaurant;
	}

	public List<RestaurantTable> getAvailableTable(Booking booking) {
		if (booking.getEndTime() == null) {
			booking.setEndTime(
			booking.getStartTime()
			.plus(restaurant.getStandardBookingDuration())
			);
		}
		return getAvailableTable(
		booking.getStartTime(),
		booking.getEndTime(),
		booking.getPartySize(),
		restaurant.canABookingOccupyALargerTable());
	}


	public List<RestaurantTable> getAvailableTable(LocalDateTime startTime,
												   int partySize,
												   boolean searchGreaterSizes) {
		LocalDateTime endTime =
		startTime.plus(restaurant.getStandardBookingDuration());
		return getAvailableTable(startTime, endTime, partySize,
		searchGreaterSizes);
	}

	public List<RestaurantTable> getAvailableTable(LocalDateTime startTime,
												   LocalDateTime endTime,
												   int partySize,
												   boolean searchGreaterSizes) {
		restaurantTableList = restaurant.getTableList();
		if (restaurantTableList == null || restaurantTableList.isEmpty()) {
			throw new IllegalStateException("Please ensure the restaurant is " +
			"set up with tables before trying to make a booking.");
		} else if (!restaurant.isBookingTime(startTime)) {
			return Collections.emptyList();
		}

		List<Booking> bookings = getBookings(startTime, endTime);

		Optional<Map<RestaurantTable, Booking>> optionalResults =
		getOccupiedTables(bookings);

		if (optionalResults.isEmpty()) {
			return Collections.emptyList();
		}

		Map<RestaurantTable, Booking> occupiedTables = optionalResults.get();
		List<RestaurantTable> foundRestaurantTables;
		foundRestaurantTables = getTableBySizeAndUpdateMap(occupiedTables,
		partySize);
		if (!foundRestaurantTables.isEmpty()) {
			return foundRestaurantTables;
		}

		if (restaurant.hasCombinationsOfTables()) {
			foundRestaurantTables = getCombinationBySizeAndUpdateMap(occupiedTables,
			partySize);
			if (!foundRestaurantTables.isEmpty()) {
				return foundRestaurantTables;
			}
		}

		if (searchGreaterSizes || partySize % 2 == 1) {
			return getATableRecursively(partySize + 1);
		}

		return Collections.emptyList();
	}

	private List<RestaurantTable> getATableRecursively(int partySize) {
		if (availableTables.containsKey(partySize)) {
			return Collections.singletonList(
			availableTables.get(partySize)
			);
		} else if (restaurant.hasCombinationsOfTables()) {
			if (availableCombinations.containsKey(partySize)) {
				return availableCombinations.get(
				partySize).getTables();
			}
		} else if (partySize >= restaurant.getLargestTableSize()) {
			return Collections.emptyList();
		}
		return getATableRecursively(partySize + 1);
	}

	private List<Booking> getBookings(LocalDateTime startTime) {
		return getBookings(startTime,
		startTime.plus(restaurant.getStandardBookingDuration()));
	}

	private List<Booking> getBookings(LocalDateTime startTime,
									  LocalDateTime endTime) {
		List<Booking> bookings =
		bookingRepository.getBookingsDuringTime(startTime,
		endTime);
		if (bookings == null) {
			throw new IllegalStateException("Connection to the booking " +
			"database failed.");
		}

		return bookings;
	}


	protected Optional<Map<RestaurantTable, Booking>> getOccupiedTables(
	List<Booking> bookings) {
		Map<RestaurantTable, Booking> occupiedTables = new HashMap<>();

		int capacityCount = 0;
		for (Booking booking : bookings) {

			// Check capacity. Return an empty list if capacity is reached.
			capacityCount += booking.getPartySize();
			if (capacityCount > restaurant.getCapacity()) {
				return Optional.empty();
			}

			booking.getTables()
			.forEach(table -> occupiedTables.put(table, booking));
		}
		return Optional.of(occupiedTables);
	}

	protected List<RestaurantTable> getTableBySizeAndUpdateMap(
	Map<RestaurantTable,
	Booking> occupiedTables, int size) {
		availableTables = new HashMap<>();
		for (RestaurantTable restaurantTable : restaurantTableList) {
			if (!occupiedTables.containsKey(restaurantTable)) {
				if (restaurantTable.getSeats() == size) {
					return Collections.singletonList(restaurantTable);
				}
				availableTables.putIfAbsent(restaurantTable.getSeats(),
				restaurantTable);
			}
		}
		return Collections.emptyList();
	}

	protected List<RestaurantTable> getCombinationBySizeAndUpdateMap(
	Map<RestaurantTable,
	Booking> occupiedTables, int size) {
		availableCombinations = new HashMap<>();
		for (CombinationOfTables combination :
		restaurant.getAllCombinationsOfTables()) {
			boolean foundAOccupiedTable = false;

			for (RestaurantTable restaurantTable : combination.getTables()) {
				if (occupiedTables.containsKey(restaurantTable)) {
					foundAOccupiedTable = true;
					break;
				}
			}

			if (!foundAOccupiedTable) {
				if (!availableCombinations.containsKey(
				combination.getSeats())) {
					if (combination.getSeats() == size) {
						return combination.getTables();
					}
					availableCombinations.put(combination.getSeats(),
					combination);
				}
			}
		}
		return Collections.emptyList();
	}

	public SortedSet<LocalTime> getAvailableTimes(int size, LocalDate date) {
		List<LocalTime> times = restaurant.getBookingTimes(date);
		SortedSet<LocalTime> availableTimes = new TreeSet<>();

		for (LocalTime time : times) {
			if (date.isEqual(LocalDate.now()) && time.isBefore(
			LocalTime.now())) {
				continue;
			}
			LocalDateTime dateTime = date.atTime(time);

			if (!getAvailableTable(dateTime, size,
			restaurant.canABookingOccupyALargerTable()).isEmpty()) {
				availableTimes.add(time);
			}
		}
		return availableTimes;
	}

	protected Map<Integer, RestaurantTable> getAvailableTablesForTest() {
		return availableTables;
	}

	protected Map<Integer, CombinationOfTables> getAvailableCombinationsForTest() {
		return availableCombinations;
	}
}

