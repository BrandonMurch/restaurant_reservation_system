package com.brandon.restaurant_reservation_system.restaurants.services;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashSet;
import java.util.List;

public class DateAvailabilityService {

	private final HashSet<LocalDate> fullDates;

	DateAvailabilityService() {
		fullDates = new HashSet<>();
	}

	public void add(LocalDate date) {
		fullDates.add(date);
	}

	public void addAll(List<LocalDate> dates) {
		fullDates.addAll(dates);
	}

	public boolean contains(LocalDate date) {
		return fullDates.contains(date);
	}

	public void remove(LocalDate date) {
		fullDates.remove(date);
	}

	public HashSet<LocalDate> getAll() {
		return fullDates;
	}

	public HashSet<LocalDate> fullDatesWithinMonth(LocalDate date) {
		Month month = date.getMonth();
		LocalDate dateToBeInspected =
				Year.from(date).atMonth(month).atDay(1);

		HashSet<LocalDate> fullDatesForMonth = new HashSet<>();
		Month monthBeingInspected = dateToBeInspected.getMonth();
		while (monthBeingInspected.equals(month)) {
			if (fullDates.contains(dateToBeInspected)) {
				fullDatesForMonth.add(dateToBeInspected);
			}
			dateToBeInspected = dateToBeInspected.plusDays(1);
			monthBeingInspected = dateToBeInspected.getMonth();
		}

		return fullDatesForMonth;
	}


}
