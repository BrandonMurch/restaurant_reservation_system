package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class TableAllocatorService {

	@Autowired
	BookingRepository bookingRepository;

	private Restaurant restaurant;
	private List<Table> tableList;
	//	private Map<Integer, List<CombinationOfTables>> movableTableCombinations;
	private Booking booking;
	private Map<Integer, Table> availableTables;
	private Map<Integer, CombinationOfTables> availableCombinations;

	public TableAllocatorService() {
	}

	//	 This constructor is for testing purposes
	protected TableAllocatorService(Restaurant restaurant, Booking booking,
	                                BookingRepository bookingRepository) {
		this(restaurant, booking);
		this.bookingRepository = bookingRepository;
	}

	public TableAllocatorService(Restaurant restaurant, Booking booking) {
		tableList = restaurant.getTableList();
		this.restaurant = restaurant;
		this.booking = booking;
	}

	public TableAllocatorService(Restaurant restaurant) {
		tableList = restaurant.getTableList();
		this.restaurant = restaurant;
	}

	public boolean bookTable(Booking booking) {
		List<Table> tableList = getAvailableTable(booking.getStartTime(),
				booking.getPartySize(),
				restaurant.canABookingOccupyALargerTable());

		if (tableList.isEmpty()) {
			return false;
		}

		booking.setTable(tableList);
		bookingRepository.save(booking);
		return true;
	}

	public List<Table> getAvailableTable(LocalDateTime startTime,
	                                     int partySize,
	                                     boolean searchGreaterSizes) {
		LocalDateTime endTime =
				startTime.plus(restaurant.getStandardBookingDuration());
		return getAvailableTable(startTime, endTime, partySize,
				searchGreaterSizes);
	}

	public List<Table> getAvailableTable(LocalDateTime startTime,
	                                     LocalDateTime endTime,
	                                     int partySize,
	                                     boolean searchGreaterSizes) {
		if (tableList == null || tableList.isEmpty()) {
			throw new IllegalStateException("Please ensure the restaurant is " +
					"set up with tables before trying to make a booking.");
		} else if (!restaurant.isBookingTime(startTime)) {
			return Collections.emptyList();
		}

		List<Booking> bookings = getBookings(startTime, endTime);
		Map<Table, Booking> occupiedTables = getOccupiedTables(bookings);
		List<Table> foundTables;
		foundTables = getAvailableTablesBySize(occupiedTables,
				partySize);
		if (!foundTables.isEmpty()) {
			return foundTables;
		}

		foundTables = getAvailableTableCombinationsBySize(occupiedTables,
				partySize);
		if (!foundTables.isEmpty()) {
			return foundTables;
		}

		return getATableRecursively(partySize + 1, searchGreaterSizes);
	}

	private List<Table> getATableRecursively(int partySize,
	                                         boolean searchGreaterSizes) {
		if (availableTables.containsKey(partySize)) {
			return Collections.singletonList(
					availableTables.get(partySize)
			);
		}

		if (restaurant.hasCombinationsOfTables()) {
			if (availableCombinations.containsKey(partySize)) {
				return availableCombinations.get(partySize).getTables();
			}
		}

		if (searchGreaterSizes) {
			if (partySize >= restaurant.getLargestTableSize()) {
				return Collections.emptyList();
			} else {
				return getATableRecursively(partySize + 1,
						true);
			}
		}
		return Collections.emptyList();
	}

	protected List<Booking> getBookings(LocalDateTime startTime) {
		return getBookings(startTime,
				startTime.plus(restaurant.getStandardBookingDuration()));
	}

	protected List<Booking> getBookings(LocalDateTime startTime,
	                                    LocalDateTime endTime) {
		List<Booking> bookings =
				bookingRepository.getBookingsDuringTime(booking.getStartTime(),
						booking.getEndTime());
		if (bookings == null) {
			throw new IllegalStateException("Connection to the booking " +
					"database failed.");
		}

		return bookings;
	}


	protected Map<Table, Booking> getOccupiedTables(List<Booking> bookings) {
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

	protected List<Table> getAvailableTablesBySize(Map<Table,
			Booking> occupiedTables, int size) {
		availableTables = new HashMap<>();
		for (Table table : tableList) {
			if (!occupiedTables.containsKey(table)
					|| !availableTables.containsKey(table.getSeats())) {
				if (table.getSeats() == size) {
					return Collections.singletonList(table);
				}
				availableTables.put(table.getSeats(), table);
			}
		}
		return Collections.emptyList();
	}

	protected List<Table> getAvailableTableCombinationsBySize(Map<Table,
			Booking> occupiedTables, int size) {
		availableCombinations = new HashMap<>();
		for (CombinationOfTables combination :
				restaurant.getCombinationsOfTables()) {
			boolean foundAOccupiedTable = false;

			for (Table table : combination.getTables()) {
				if (occupiedTables.containsKey(table)) {
					foundAOccupiedTable = true;
					break;
				}
			}

			if (!foundAOccupiedTable) {
				if (!availableCombinations.containsKey(
						combination.getTotalSeats())) {
					if (combination.getTotalSeats() == size) {
						return combination.getTables();
					}
					availableCombinations.put(combination.getTotalSeats(),
							combination);
				}
			}
		}
		return Collections.emptyList();
	}


	public Set<LocalTime> getAvailableTimes(int size, LocalDate date) {
		List<LocalTime> times = restaurant.getBookingTimes(date);
		SortedSet<LocalTime> availableTimes = new TreeSet<>();

		for (LocalTime time : times) {
			LocalDateTime dateTime = date.atTime(time);

			if (!getAvailableTable(dateTime, size,
					restaurant.canABookingOccupyALargerTable()).isEmpty()) {
				availableTimes.add(time);
			}
		}
		return availableTimes;
	}

	public SortedSet<LocalDate> getAvailableDates() {
		DateRange dates = restaurant.getBookingDateRange();
		LocalDate current = dates.getStart();
		LocalDate end = dates.getEnd().plusDays(1);

		SortedSet<LocalDate> availableDates = new TreeSet<>();

		while (current.isBefore(end)) {
			if (restaurant.isOpenOnDate(current)
					&& isDateAvailable(current)) {
				availableDates.add(current);
			}
		}

		return availableDates;

	}

	public boolean isDateAvailable(LocalDate date) {
		List<LocalTime> times = restaurant.getBookingTimes(date);
		for (LocalTime time : times) {
			LocalDateTime dateTime = date.atTime(time);

			if (!getAvailableTable(dateTime, 2,
					true).isEmpty()) {
				return true;
			}
		}
		return false;
	}


}

