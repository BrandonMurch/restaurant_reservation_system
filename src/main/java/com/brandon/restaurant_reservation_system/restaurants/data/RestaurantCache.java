/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class RestaurantCache {

	@Autowired
	private Restaurant restaurant;
	@Autowired
	private TableAllocatorService tableAllocatorService;
	private LocalDate dateThatDatesLastUpdated;
	private SortedSet<LocalDate> availableDates = new TreeSet<>();

	public RestaurantCache() {
	}

	@PostConstruct
	public void postConstruct() {
	}


	public SortedSet<LocalDate> getAvailableDates() {
		checkCache();
		return availableDates;
	}

	public void addAvailableDate(LocalDate date) {
		checkCache();
		availableDates.add(date);
	}

	public void removeDateIfUnavailable(LocalDate date) {
		checkCache();
		if (!tryBookingOnDate(date)) {
			availableDates.remove(date);
		}
	}

	protected void checkCache() {
		if (availableDates == null
		|| availableDates.isEmpty()
		|| !dateThatDatesLastUpdated.isEqual(LocalDate.now())) {
			createCache();
		}
	}

	protected void createCache() {
		DateRange dates = restaurant.getBookingDateRange();
		LocalDate current = dates.getStart();
		LocalDate end = dates.getEnd().plusDays(1);

		availableDates = new TreeSet<>();

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

