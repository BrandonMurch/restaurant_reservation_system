/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.SortedSet;

@Component
public class RestaurantCache {

	@Autowired
	private TableAllocatorService tableAllocatorService;
	private LocalDate dateThatDatesLastUpdated;
	private SortedSet<LocalDate> availableDates;

	public RestaurantCache() {
	}

	private void refreshCache() {
		System.out.println("Refresh happened");
		availableDates = tableAllocatorService.getAvailableDates();
		dateThatDatesLastUpdated = LocalDate.now();
	}

	public void checkCache() {
		if ((availableDates == null)
		|| availableDates.isEmpty()
		|| !dateThatDatesLastUpdated.isEqual(LocalDate.now())) {
			refreshCache();
		}
	}

	public SortedSet<LocalDate> getAvailableDates() {
		checkCache();
		return availableDates;
	}

	public void addAvailableDate(LocalDate date) {
		checkCache();
		availableDates.add(date);
	}

	public void removeAvailableDate(LocalDate date) {
		checkCache();
		availableDates.remove(date);
	}

	public void setAvailableDates(
	SortedSet<LocalDate> availableDates) {
		dateThatDatesLastUpdated = LocalDate.now();
		this.availableDates = availableDates;
	}
}

