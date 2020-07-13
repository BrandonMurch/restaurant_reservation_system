package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class RestaurantCache {

	@Autowired
	private TableAllocatorService tableAllocatorService;

	private LocalDate dateThatDatesLastUpdated;
	private SortedSet<LocalDate> availableDates = new TreeSet<>();

	public RestaurantCache() {
	}

	public SortedSet<LocalDate> getAvailableDates() {
		if ((availableDates == null)
				|| availableDates.isEmpty()
				|| !dateThatDatesLastUpdated.isEqual(LocalDate.now())) {
			availableDates = tableAllocatorService.getAvailableDates();
			dateThatDatesLastUpdated = LocalDate.now();
		}
		return availableDates;
	}

	public void addAvailableDate(LocalDate date) {
		availableDates.add(date);
	}

	public void removeAvailableDate(LocalDate date) {
		availableDates.remove(date);
	}

	public void setAvailableDates(
			SortedSet<LocalDate> availableDates) {
		dateThatDatesLastUpdated = LocalDate.now();
		this.availableDates = availableDates;
	}
}

