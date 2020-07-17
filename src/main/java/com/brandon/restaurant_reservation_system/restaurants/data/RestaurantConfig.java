/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import java.io.Serializable;
import java.time.Duration;

public class RestaurantConfig implements Serializable {

	private static final long serialVersionUID = 7979825971594010456L;
	private int capacity;
	private boolean limitPeoplePerInterval;
	private int peoplePerInterval;
	private boolean canABookingOccupyALargerTable;
	private Duration standardBookingDuration;
	private int largestTableSize;

	// Constructor
	public RestaurantConfig() {
		capacity = 0;
		limitPeoplePerInterval = false;
		peoplePerInterval = 0;
		canABookingOccupyALargerTable = false;
		standardBookingDuration = Duration.ZERO;
		largestTableSize = 0;
	}

	// getters

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public boolean arePeopleLimitedPerInterval() {
		return limitPeoplePerInterval;
	}

	public int getPeoplePerInterval() {
		return peoplePerInterval;
	}

	public void setPeoplePerInterval(int peoplePerInterval) {
		this.limitPeoplePerInterval = peoplePerInterval != 0;
		this.peoplePerInterval = peoplePerInterval;
	}

	public boolean canABookingOccupyALargerTable() {
		return canABookingOccupyALargerTable;
	}

	public Duration getStandardBookingDuration() {
		return standardBookingDuration;
	}


	public void limitPeoplePerTimeInterval(int peoplePerInterval) {
		limitPeoplePerInterval = true;
		this.peoplePerInterval = peoplePerInterval;
	}

	public void noLimitPeoplePerTimeInterval() {
		limitPeoplePerInterval = false;
		this.peoplePerInterval = 0;
	}

	public void setStandardBookingDuration(int durationInMinutes) {
		this.standardBookingDuration = Duration.ofMinutes(durationInMinutes);

	}

	public void setCanABookingOccupyALargerTable(boolean bool) {
		this.canABookingOccupyALargerTable = bool;
	}

	public int getLargestTableSize() {
		return largestTableSize;
	}

	public void setLargestTableSize(int largestTableSize) {
		this.largestTableSize = largestTableSize;
	}
}
