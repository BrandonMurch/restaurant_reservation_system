package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
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
	@Autowired
	private BookingHandlerService bookingService;

	private List<RestaurantTable> restaurantTableList;
	//	private Map<Integer, List<CombinationOfTables>> movableTableCombinations;
	private Map<Integer, RestaurantTable> availableTables;
	private Map<Integer, CombinationOfTables> availableCombinations;

	public TableAllocatorService() {
	}

	//		 This constructor is for testing purposes
	protected TableAllocatorService(Restaurant restaurant,
	                                BookingRepository bookingRepository) {
		this(restaurant);
		this.bookingRepository = bookingRepository;
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
		Map<RestaurantTable, Booking> occupiedTables = getOccupiedTables(
				bookings);
		List<RestaurantTable> foundRestaurantTables;
		foundRestaurantTables = getTableBySizeAndUpdateMap(occupiedTables,
				partySize);
		if (!foundRestaurantTables.isEmpty()) {
			return foundRestaurantTables;
		}

		foundRestaurantTables = getCombinationBySizeAndUpdateMap(occupiedTables,
				partySize);
		if (!foundRestaurantTables.isEmpty()) {
			return foundRestaurantTables;
		}

		return getATableRecursively(partySize + 1, searchGreaterSizes);
	}

	private List<RestaurantTable> getATableRecursively(int partySize,
	                                                   boolean searchGreaterSizes) {
		if (availableTables.containsKey(partySize)) {
			return Collections.singletonList(
					availableTables.get(partySize)
			);
		}

		if (restaurant.hasCombinationsOfTables()) {
			if (availableCombinations.containsKey(partySize)) {
				return availableCombinations.get(
						partySize).getRestaurantTables();
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
				bookingRepository.getBookingsDuringTime(startTime,
						endTime);
		if (bookings == null) {
			throw new IllegalStateException("Connection to the booking " +
					"database failed.");
		}

		return bookings;
	}


	protected Map<RestaurantTable, Booking> getOccupiedTables(
			List<Booking> bookings) {
		Map<RestaurantTable, Booking> occupiedTables = new HashMap<>();

		int capacityCount = 0;
		for (Booking booking : bookings) {

			// Check capacity. Return an empty list if capacity is reached.
			capacityCount += booking.getPartySize();
			if (capacityCount > restaurant.getCapacity()) {
				return Collections.emptyMap();
			}

			booking.getTables()
					.forEach(table -> occupiedTables.put(table, booking));
		}
		return occupiedTables;
	}

	protected List<RestaurantTable> getTableBySizeAndUpdateMap(
			Map<RestaurantTable,
					Booking> occupiedTables, int size) {
		availableTables = new HashMap<>();
		for (RestaurantTable restaurantTable : restaurantTableList) {
			if (!occupiedTables.containsKey(restaurantTable)
					|| !availableTables.containsKey(
					restaurantTable.getSeats())) {
				if (restaurantTable.getSeats() == size) {
					return Collections.singletonList(restaurantTable);
				}
				availableTables.put(restaurantTable.getSeats(),
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
				restaurant.getCombinationsOfTables()) {
			boolean foundAOccupiedTable = false;

			for (RestaurantTable restaurantTable : combination.getRestaurantTables()) {
				if (occupiedTables.containsKey(restaurantTable)) {
					foundAOccupiedTable = true;
					break;
				}
			}

			if (!foundAOccupiedTable) {
				if (!availableCombinations.containsKey(
						combination.getTotalSeats())) {
					if (combination.getTotalSeats() == size) {
						return combination.getRestaurantTables();
					}
					availableCombinations.put(combination.getTotalSeats(),
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
			current = current.plusDays(1);
		}

		return availableDates;

	}

	public boolean isDateAvailable(LocalDate date) {
		List<LocalTime> times = restaurant.getBookingTimes(date);
		for (LocalTime time : times) {
			LocalDateTime dateTime = date.atTime(time);

			if (!getAvailableTable(dateTime, 2,
					restaurant.canABookingOccupyALargerTable()).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void removeDateIfUnavailable(LocalDate date) {
		if (!isDateAvailable(date)) {
			restaurant.removeAvailableDate(date);
		}
	}


}

