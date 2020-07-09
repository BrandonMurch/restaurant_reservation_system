package com.brandon.restaurant_reservation_system.restaurants.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Day implements Serializable {

	private static final long serialVersionUID = -1607064466422474736L;
	// instance variables
	private final DayOfWeek dayOfWeek;
	private boolean isOpen;
	private final List<OpeningClosingPair> hoursOfOperation;

	//constructors
	public Day(DayOfWeek dayOfWeek, boolean isOpen) {
		this.dayOfWeek = dayOfWeek;
		hoursOfOperation = new ArrayList<>();
		this.isOpen = isOpen;
	}

	public Day(DayOfWeek dayOfWeek,
	           List<OpeningClosingPair> hoursOfOperation) {
		this.dayOfWeek = dayOfWeek;
		this.hoursOfOperation = hoursOfOperation;
		this.isOpen = true;
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
