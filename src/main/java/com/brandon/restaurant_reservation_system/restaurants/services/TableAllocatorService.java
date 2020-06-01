package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.controller.BookingController;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.helpers.data_sorting.DateTimeSorting;
import com.brandon.restaurant_reservation_system.restaurants.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;


@Service
public class TableAllocatorService {

	private final int partySize;
	private final LocalDateTime bookingDateTime;
	private final Restaurant restaurant;
	private final List<Table> tableList;
	private final List<CombinationOfTables> combinationOfTablesList;
	private final boolean hasCombinations;
	private final Map<Integer, List<CombinationOfTables>> availableTableCombinations;
	private final Map<Integer, List<CombinationOfTables>> movableTableCombinations;
	private final Booking booking;
	@Autowired
	BookingRepository bookingRepository;
	private List<Booking> bookings;
	private Map<Table, Booking> occupiedTables;
	private Map<Integer, List<Table>> availableTables;

	// todo this won't load with spring.
	public TableAllocatorService() {
		Restaurant restaurant = new Restaurant();
		tableList = restaurant.getTableList();
		combinationOfTablesList = restaurant.getCombinationsOfTables();
		this.restaurant = restaurant;
		availableTableCombinations = new HashMap<>();
		movableTableCombinations = new HashMap<>();
		this.booking = new Booking();
		this.partySize = 0;
		this.bookingDateTime = booking.getStartTime();

		hasCombinations = combinationOfTablesList != null
				&& !combinationOfTablesList.isEmpty();

	}

	//	 This constructor is for testing purposes
	public TableAllocatorService(Restaurant restaurant, Booking booking,
	                             BookingRepository bookingRepository) {
		this(restaurant, booking);
		this.bookingRepository = bookingRepository;
		testRefresh();
	}

	public TableAllocatorService(Restaurant restaurant, Booking booking) {
		tableList = restaurant.getTableList();
		combinationOfTablesList = restaurant.getCombinationsOfTables();
		this.restaurant = restaurant;
		availableTableCombinations = new HashMap<>();
		movableTableCombinations = new HashMap<>();
		this.booking = booking;
		this.partySize = booking.getPartySize();
		this.bookingDateTime = booking.getStartTime();

		hasCombinations = combinationOfTablesList != null
				&& !combinationOfTablesList.isEmpty();
	}

	// to update the bookings, tables, etc. when testing
	protected void testRefresh() {
		bookings = getBookings();
		occupiedTables = findOccupiedTables();
		availableTables = findAvailableTables();
		mapTableCombinations();
	}

	protected List<Booking> getBookings() {
		return new BookingController()
				.getBookingsDuringTime(booking.getStartTime().toString(),
						booking.getEndTime().toString());
	}

	protected Map<Table, Booking> findOccupiedTables() {
		Map<Table, Booking> occupiedTables = new HashMap<>();
		int capacityCount = 0;
		for (Booking booking : bookings) {

			// Check capacity. Return an empty list if capacity is reached.
			capacityCount += booking.getPartySize();
			if (capacityCount > restaurant.getCapacity()) {
				return Collections.emptyMap();
			}

			booking.getTable()
					.forEach(table -> occupiedTables.put(table, booking));
		}
		return occupiedTables;
	}

	protected Map<Integer, List<Table>> findAvailableTables() {

		// Remove the occupied tables from all tables and place in hashmap
		HashMap<Integer, List<Table>> availableTables = new HashMap<>();
		for (Table table : tableList) {
			if (occupiedTables.containsKey(table)) {
				continue;
			}

			availableTables.computeIfAbsent(table.getSeats(), k ->
					new ArrayList<>()).add(table);
		}

		return availableTables;
	}

	protected void mapTableCombinations() {
		for (CombinationOfTables combination : combinationOfTablesList) {
			boolean foundAOccupiedTable = false;
			if (isCombinationFullyBooked(combination)) {
				continue;
			}

			// some occupied tables means they might be movable
			for (Table table : combination.getTables()) {
				if (occupiedTables.containsKey(table)) {
					foundAOccupiedTable = true;
					movableTableCombinations
							.computeIfAbsent(combination.getTotalSeats(), k ->
									new ArrayList<>()).add(combination);
					break;
				}
			}
			// Free tables
			if (!foundAOccupiedTable) {
				availableTableCombinations
						.computeIfAbsent(combination.getTotalSeats(), k ->
								new ArrayList<>()).add(combination);
			}
		}

	}

	protected boolean isCombinationFullyBooked(
			CombinationOfTables combination) {
		int count = 0;
		for (Table table : combination.getTables()) {
			if (occupiedTables.containsKey(table)) {
				count++;
			}
		}
		return count == combination.getTables().size();
	}

	// TODO if the restaurant is closed, linked back the closest opening
	//  time, and any open reservation slots on that day if the particular
	//  time is booked. - would work best in controller, http codes to mean
	//  different outcomes
	public boolean bookTable() {
		// get free table
		List<Table> tableList = getAFreeTable();
		if (tableList.isEmpty()) {
			return false;
		}

		// TODO how to properly connect the table to the booking
		//  -- This should probably happen in the booking controller
		// TODO how to link back open reservation slots
		// change table in booking to free table
		booking.setTable(tableList);
		// connect booking to table, save in repository
		// todo after booking, check to see if restaurant is full, if so, add
		//  to the hashset.
		return true; // to compile.
	}

	protected boolean isTheRestaurantOpen(LocalDateTime date) {

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		Day day = restaurant.getOpeningHours(dayOfWeek);

		if (!day.isOpen()) {
			return false;
		}

		LocalTime time = date.toLocalTime();
		if (day.getPairThatContainsTime(time).isPresent()) {
			return true;
		}
		return false;
	}

	public List<Table> getAFreeTable() {
		return getAFreeTable(bookingDateTime);
	}

			// todo check here if the booking time is in line with the reservation
	//  slots, if applicable. If not, return false, or modify it to the
	//  closest one.
	// todo if restaurant uses booking slot intervals, find the closest one
	// todo check date in a hashset to see if it is full
	public List<Table> getAFreeTable(LocalDateTime bookingDateTime) {
		// There are no tables in the restaurant

		if (!isTheRestaurantOpen(bookingDateTime)) {
			return Collections.emptyList();
		}
		if (tableList == null || tableList.isEmpty()) {
			throw new IllegalStateException("Please ensure the restaurant is " +
					"set up with tables before trying to make a booking.");
		}

		// get bookings for specific time
		bookings = getBookings();
		if (bookings == null) {
			throw new IllegalStateException("Connection to the booking " +
					"database failed.");
		}

		occupiedTables = findOccupiedTables();

		availableTables = findAvailableTables();

		// if all tables were full, nothing would be entered into the map
		if (availableTables.isEmpty()) {
			return Collections.emptyList();
		}

		// Find single tables that match the party size.
		List<Table> tablesEqualToPartySize =
				findTablesTryOddNumbersAsEvens(this.partySize);
		if (!tablesEqualToPartySize.isEmpty()) {
			return tablesEqualToPartySize;
		}

		// if there are no combinations set up, do not perform searches on
		// combinations.
		if (hasCombinations) {
			mapTableCombinations();
			// Find combinations that match the party size.
			List<Table> results =
					findCombinationsTryOddNumbersAsEvens(this.partySize);
			if (!results.isEmpty()) {
				return results;
			}
		}

		if (restaurant.canABookingOccupyALargerTable()) {
			return findTablesAndCombinationsGreaterSize();
		} else {
			return Collections.emptyList();
		}
	}

	protected List<Table> findTablesTryOddNumbersAsEvens(int partySize) {
		int end = partySize % 2 == 1 ? partySize + 1 : partySize;
		for (int numberOfSeats = partySize; numberOfSeats <= end; numberOfSeats++) {
			List<Table> result = findTables(partySize);
			if (!result.isEmpty()) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	protected List<Table> findTables(int partySize) {
		if (availableTables.containsKey(partySize)) {
			return Collections.singletonList(
					availableTables.get(partySize).get(0));
		} else {
			return Collections.emptyList();
		}
	}

	// numberOfPeople is odd, see if there is a table that is 1 greater.
	// ex. 5 to 6
	protected List<Table> findCombinationsTryOddNumbersAsEvens(int partySize) {
		int end = partySize % 2 == 1 ? partySize + 1 : partySize;
		for (int numberOfSeats = partySize; numberOfSeats <= end; numberOfSeats++) {
			List<Table> result = findCombinations(partySize);
			if (!result.isEmpty()) {
				return result;
			}
		}
		return Collections.emptyList();
	}


	protected List<Table> findCombinations(int partySize) {

		if (availableTableCombinations.containsKey(partySize)) {
			return availableTableCombinations
					.get(partySize)
					.get(0)
					.getTables();
		}

		List<Table> movableTables = findTableSwapsInAllCombinations(partySize);
		if (!movableTables.isEmpty()) {
			return movableTables;
		}

		return Collections.emptyList();
	}

	protected List<Table> findTablesAndCombinationsGreaterSize() {

		// Store sizes of maps/lists in memory, or 0 if there are none
		SortedSet<Integer> keys = new TreeSet<>();
		keys.addAll(availableTableCombinations.keySet());
		keys.addAll(movableTableCombinations.keySet());
		keys.addAll(availableTables.keySet());

		// reduce the set to only contain tables with a size greater than
		// partySize
		SortedSet<Integer> reducedKeys = keys.tailSet(partySize + 2);

		for (int numberOfSeats : reducedKeys) {
			List<Table> result = findTables(numberOfSeats);
			if (!result.isEmpty()) {
				return result;
			}
			result = findCombinations(numberOfSeats);
			if (!result.isEmpty()) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	protected List<Table> findTableSwapsInAllCombinations(int numberOfSeats) {

		if (movableTableCombinations.containsKey(numberOfSeats)) {
			List<CombinationOfTables> combinations =
					movableTableCombinations.get(numberOfSeats);
			// try to swap the tables out on each movable combination
			for (CombinationOfTables combination : combinations) {
				boolean didASwapOccur = findAllTableSwaps(combination);
				if (didASwapOccur) {
					return combination.getTables();
				}
			}
		}
		return Collections.emptyList();
	}

	// todo break this into two methods?
	protected boolean findAllTableSwaps(
			CombinationOfTables combination) {

		Map<List<Table>, List<Table>> swapsToPerform
				= new HashMap<>();

		for (Table table : combination.getTables()) {
			int seats = table.getSeats();
			if (occupiedTables.containsKey(table)) {
				// booking contains a combination of tables
				List<Table> bookingTables =
						occupiedTables.get(table).getTable();

				// swap combinations
				if (bookingTables.size() > 1) {
					{
						if (!availableTableCombinations.containsKey(seats)) {
							return false;
						}
					}
					CombinationOfTables combinationToSwap =
							availableTableCombinations.get(seats).get(0);
					swapsToPerform.put(bookingTables,
							combinationToSwap.getTables());
					swapTablesInBookings(swapsToPerform);
					return true;
				}

				// swap single tables
				if (!availableTables.containsKey(seats)) {
					return false;
				}
				List<Table> swappableTables = availableTables.get(seats);
				if (swappableTables.isEmpty()) {
					break;
				}
				boolean swapFound = false;
				for (Table potentialSwap : swappableTables) {
					if (!combination.getTables().contains(potentialSwap)
							|| !occupiedTables.containsKey(potentialSwap)) {
						swapsToPerform.put(Collections.singletonList(table),
								Collections.singletonList(potentialSwap));
						swapFound = true;
						break;
					}
				}
				// there were no available tables for the current booked
				// table to move to.
				if (!swapFound) {
					return false;
				}
			}
		}
		swapTablesInBookings(swapsToPerform);
		// All swaps have been found
		return true;
	}

	protected void swapTablesInBookings(
			Map<List<Table>, List<Table>> swapsToPerform) {
		for (List<Table> tables : swapsToPerform.keySet()) {
			Booking booking = occupiedTables.get(tables.get(0));
			booking.setTable(swapsToPerform.get(tables));
		}
	}

	public Optional<LocalDateTime> getClosestAvailableTime() {

		Day day = restaurant.getOpeningHours(bookingDateTime.getDayOfWeek());
		LocalTime bookingTime = this.bookingDateTime.toLocalTime();

		if (day.areBookingsOnlyAtCertainTimes()) {
			List<LocalTime> possibleBookingTimes = day.getBookingTimes();

			int indexOfBookingTime = DateTimeSorting.findClosestIndex(possibleBookingTimes,
					bookingTime);
			int secondHalfSize = possibleBookingTimes.size() - indexOfBookingTime - 1;
			int end = Math.max(indexOfBookingTime, secondHalfSize);
			for (int i = 0; i < end; i++) {
				if (i < indexOfBookingTime) {
					LocalTime bookingSlotTimes = possibleBookingTimes.get(
							indexOfBookingTime - i);
					LocalDateTime dateTime = bookingSlotTimes
							.atDate(bookingDateTime.toLocalDate());
					List<Table> tables = getAFreeTable(dateTime);
					if (!tables.isEmpty()) {
						return Optional.of(dateTime);
					}
				}
				if (i < secondHalfSize) {
					LocalTime bookingSlotTimes = possibleBookingTimes.get(indexOfBookingTime + i);
					LocalDateTime dateTime = bookingSlotTimes
							.atDate(bookingDateTime.toLocalDate());
					List<Table> tables = getAFreeTable(dateTime);
					if (!tables.isEmpty()) {
						return Optional.of(dateTime);
					}
				}
			}
			return Optional.empty();
			//go through certain times
		} else {
			Duration interval = day.getBookingSlotIntervals();
			Optional<OpeningClosingPair> optionalHoursOfOperation =
					day.getPairThatContainsTime(bookingTime);

			if (optionalHoursOfOperation.isEmpty()) {
				return Optional.empty();
			}

			LocalDate bookingDate = bookingDateTime.toLocalDate();

			LocalDateTime opening =
					optionalHoursOfOperation.get().getOpening().atDate(bookingDate);
			LocalDateTime closing =
					optionalHoursOfOperation.get().getClosing().atDate(bookingDate);

			LocalDateTime earlier = bookingDateTime;
			LocalDateTime later = bookingDateTime;
			// todo then swapping back to dateTime
			while (earlier.isAfter(opening)
					|| later.isBefore(closing)) {

				earlier = LocalDateTime.from(interval.subtractFrom(earlier));
				if (!getAFreeTable(earlier).isEmpty()) {
					return Optional.of(earlier);
				}

				later = LocalDateTime.from(interval.addTo(bookingTime));
				if (!getAFreeTable(later).isEmpty()) {
					return Optional.of(later);
				}
			}
		}
		return Optional.empty();
	}




}

