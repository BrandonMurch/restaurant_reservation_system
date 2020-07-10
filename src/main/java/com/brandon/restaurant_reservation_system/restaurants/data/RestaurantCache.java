package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class RestaurantCache {

	private Restaurant restaurant;

	@Autowired
	private TableAllocatorService tableAllocatorService;

	private LocalDate dateThatDatesLastUpdated;
	private LocalDate dateThatTimesLastUpdated;
	private SortedSet<LocalDate> availableDates = new TreeSet<>();
	private Map<String, SortedSet<LocalTime>> availableTimes = new HashMap<>();

	public RestaurantCache() {
	}

	public RestaurantCache(Restaurant restaurant) {
		this.restaurant = restaurant;
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

	public void setAvailableDates(
			SortedSet<LocalDate> availableDates) {
		dateThatDatesLastUpdated = LocalDate.now();
		this.availableDates = availableDates;
	}

	// FIXME: How do we know which sizes, and dates have been updated?
	public SortedSet<LocalTime> getAvailableTimes(LocalDate date,
	                                              int size) {
		String key = date.toString() + size;
		if (!availableTimes.containsKey(key)) {
			SortedSet<LocalTime> times =
					tableAllocatorService.getAvailableTimes(size, date);
			availableTimes.put(key, times);
			return times;
		}
		return availableTimes.get(key);
	}

	public void setAvailableTimes(
			Map<String, SortedSet<LocalTime>> availableTimes) {
		dateThatTimesLastUpdated = LocalDate.now();
		this.availableTimes = availableTimes;
	}
}
