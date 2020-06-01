package com.brandon.restaurant_reservation_system.restaurants.data;

import java.time.Duration;

public class RestaurantConfig {

	// true == booking times, false == bookingSlotIntervals
	private int capacity;
	private boolean limitPeoplePerInterval;
	private int peoplePerInterval;
	private boolean canABookingOccupyALargerTable;
	private Duration standardBookingDuration;

	// Constructor
	public RestaurantConfig() {
		capacity = 0;
		limitPeoplePerInterval = false;
		peoplePerInterval = 0;
		canABookingOccupyALargerTable = false;
		standardBookingDuration = Duration.ZERO;
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

}
