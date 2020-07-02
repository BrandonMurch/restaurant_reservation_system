package com.brandon.restaurant_reservation_system.restaurants.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BookingTimes {

	private boolean bookingsAtCertainTimes;
	private List<LocalTime> bookingTimes;
	private Duration bookingSlotIntervals;
	private Map<DayOfWeek, Day> openingHours;

	public BookingTimes() {
	}

	public BookingTimes(List<LocalTime> bookingTimes) {
		bookingsAtCertainTimes = true;
		this.bookingTimes = bookingTimes;
		initOpeningHours();
	}

	private void initOpeningHours() {
		this.openingHours = new HashMap<>();
		for (DayOfWeek day : DayOfWeek.values()) {
			this.openingHours.put(day, new Day(day, false));
		}
	}

	public BookingTimes(int minutesBetweenBookingSlots) {
		bookingSlotIntervals = Duration.ofMinutes(minutesBetweenBookingSlots);
	}

	public Map<DayOfWeek, Day> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(
			Map<DayOfWeek, Day> openingHours) {
		this.openingHours = openingHours;
	}

	public Day getOpeningHours(DayOfWeek dayOfWeek) {
		return openingHours.get(dayOfWeek);
	}

	public List<LocalTime> getBookingTimes(LocalDate date) {
		Day day = openingHours.get(date.getDayOfWeek());
		if (!day.isOpen()) {
			return Collections.emptyList();
		} else if (bookingsAtCertainTimes) {
			return this.bookingTimes;
		}
		List<LocalTime> bookingTimes = new ArrayList<>();
		List<OpeningClosingPair> openClosePairs = day.getOpeningPairs();
		for (OpeningClosingPair pair : openClosePairs) {
			LocalTime time = pair.getOpening();
			LocalTime closing = pair.getClosing();
			while (time.isBefore(closing)) {
				bookingTimes.add(time);
				time.plus(bookingSlotIntervals);
			}
		}

		return bookingTimes;
	}

	public void allowBookingsOnlyAtCertainTimes(List<LocalTime> bookingTimes) {
		this.bookingsAtCertainTimes = true;
		this.bookingTimes = bookingTimes;
	}

	public void allowBookingPerTimeInterval(int bookingIntervalInMinutes) {
		bookingsAtCertainTimes = false;
		this.bookingSlotIntervals =
				Duration.ofMinutes(bookingIntervalInMinutes);
	}


}
