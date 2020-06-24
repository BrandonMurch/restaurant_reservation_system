package com.brandon.restaurant_reservation_system.restaurants.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateAvailabilityServiceTest {

	private final DateAvailabilityService service = new DateAvailabilityService();
	private final List<LocalDate> list = getDummyDates();

	@Test
	void add() {
		service.add(list.get(0));
		HashSet<LocalDate> set = service.getAll();
		assertTrue(set.contains(list.get(0)));
	}

	@Test
	void contains() {
		service.add(list.get(0));
		assertTrue(service.contains(list.get(0)));
	}

	@Test
	void remove() {
		service.remove(list.get(0));
		HashSet<LocalDate> set = service.getAll();
		assertFalse(set.contains(list.get(0)));
	}

	@Test
	void addAll() {
		service.addAll(list);
		HashSet<LocalDate> set = service.getAll();
		assertTrue(set.contains(list.get(2)));
	}

	@Test
	void fullDatesWithinMonth() {
		service.addAll(list);
		HashSet<LocalDate> set = service.fullDatesWithinMonth(list.get(0));
		assertTrue(set.contains(list.get(1)));
	}

	public List<LocalDate> getDummyDates() {
		LocalDate date = Year.now().atMonth(1).atDay(1);
		List<LocalDate> list = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			date = date.plusDays(2);
			list.add(date);
		}

		return list;
	}


}