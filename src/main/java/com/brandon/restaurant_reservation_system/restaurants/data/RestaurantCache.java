/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
public class RestaurantCache {

	@Autowired
	private Restaurant restaurant;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private TableAllocatorService tableAllocatorService;
	private LocalDate dateThatDatesLastUpdated;
	private final SortedSet<LocalDate> availableDates = new TreeSet<>();
	private Map<LocalDate, Integer> bookingsPerDate = new HashMap<>();


	public RestaurantCache() {
	}

	public Map<LocalDate, Integer> getBookingsPerDate() {
		checkBookingsPerDate();
		return bookingsPerDate;
	}

	public void addBookingToDate(LocalDate date, Integer numberOfBookings) {
		checkBookingsPerDate();
		bookingsPerDate.merge(date, numberOfBookings, Integer::sum);
	}

	private Integer getDifference(Integer value1, Integer value2) {
		return value1 - value2;
	}

	public void removeBookingFromDate(LocalDate date, Integer numberOfBookings) {
		checkBookingsPerDate();
		bookingsPerDate.merge(date, numberOfBookings, this::getDifference);

		if (!tryBookingOnDate(date)) {
			availableDates.remove(date);
		}
	}

	protected void checkBookingsPerDate() {
		if (bookingsPerDate == null
		|| bookingsPerDate.isEmpty()) {
			createBookingsPerDate();
		}
	}

	protected void createBookingsPerDate() {
		bookingsPerDate = bookingRepository.getCountByDayMap();
	}

	public SortedSet<LocalDate> getAvailableDates() {
		checkAvailableDatesCache();
		return availableDates;
	}

	public void addAvailableDate(LocalDate date) {
		checkAvailableDatesCache();
		availableDates.add(date);
	}

	public void removeDateIfUnavailable(LocalDate date) {
		checkAvailableDatesCache();
		if (!tryBookingOnDate(date)) {
			availableDates.remove(date);
		}
	}

	protected void checkAvailableDatesCache() {
		if (availableDates.isEmpty()
		|| !dateThatDatesLastUpdated.isEqual(LocalDate.now())) {
			createAvailableDatesCache();
		}
	}

	protected void createAvailableDatesCache() {
		DateRange dates = restaurant.getBookingDateRange();
		LocalDate current = dates.getStart();
		LocalDate end = dates.getEnd().plusDays(1);

		while (current.isBefore(end)) {
			if (restaurant.isOpenOnDate(current)
			&& tryBookingOnDate(current)) {
				availableDates.add(current);
			}
			current = current.plusDays(1);
		}
		dateThatDatesLastUpdated = LocalDate.now();
	}

	private boolean tryBookingOnDate(LocalDate date) {
		List<LocalTime> times = restaurant.getBookingTimes(date);
		for (LocalTime time : times) {
			LocalDateTime dateTime = date.atTime(time);

			if (!tableAllocatorService.getAvailableTable(dateTime, 2,
			false).isEmpty()) {
				return true;
			}
		}
		return false;
	}
}

