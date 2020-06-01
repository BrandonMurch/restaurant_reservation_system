package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.Entity;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Entity
public class Day {

	// todo redo this so it can also hold dates

	private final DayOfWeek dayOfWeek;
	private boolean isOpen;
	private List<OpeningClosingPair> hoursOfOperation;
	private List<LocalTime> bookingTimes;
	private boolean bookingsAtCertainTimes;
	private Duration bookingSlotIntervals;

	public Day(DayOfWeek dayOfWeek, boolean isOpen) {
		this.dayOfWeek = dayOfWeek;
		hoursOfOperation = new ArrayList<>();
		bookingTimes = new ArrayList<>();
		this.isOpen = isOpen;
		bookingsAtCertainTimes = false;
		bookingTimes = new ArrayList<>();
		bookingSlotIntervals = Duration.ZERO;

	}


	public Day(DayOfWeek dayOfWeek,
	           List<OpeningClosingPair> hoursOfOperation) {
		this.dayOfWeek = dayOfWeek;
		this.hoursOfOperation = hoursOfOperation;
		this.isOpen = true;
		bookingTimes = new ArrayList<>();
		bookingsAtCertainTimes = false;
		bookingTimes = new ArrayList<>();
		bookingSlotIntervals = Duration.ZERO;

	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public String getDayOfWeekAsString() {
		return dayOfWeek.toString();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public List<OpeningClosingPair> getOpeningPairs() {
		return hoursOfOperation;
	}

	public void addOpeningAndClosing(LocalTime opening, LocalTime closing) {
		this.hoursOfOperation.add(new OpeningClosingPair(opening, closing));
	}

	public void removeOpeningAndClosing(LocalTime opening, LocalTime closing) {
		Iterator<OpeningClosingPair> itr = this.hoursOfOperation.iterator();
		while (itr.hasNext()) {
			OpeningClosingPair nextPair = itr.next();
			if (opening.equals(nextPair.getOpening())
					&& closing.equals(nextPair.getClosing())) {
				itr.remove();
				break;
			}
		}
	}

	public boolean areBookingsOnlyAtCertainTimes() {
		return bookingsAtCertainTimes;
	}

	public Duration getBookingSlotIntervals() {
		return bookingSlotIntervals;
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

	public List<LocalTime> getBookingTimes() {
		return bookingTimes;
	}

	public Optional<OpeningClosingPair> getPairThatContainsTime(LocalTime time) {
		for (OpeningClosingPair pair : this.hoursOfOperation) {
			if (time.isAfter(pair.getOpening()) && time.isBefore(pair.getClosing())) {
				return Optional.of(pair);
			}
		}
		return Optional.empty();

	}


	@Override
	public String toString() {
		return dayOfWeek + ": " + (isOpen() ? "Open" : "Closed");
	}
}
