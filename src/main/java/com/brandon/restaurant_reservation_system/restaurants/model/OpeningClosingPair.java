package com.brandon.restaurant_reservation_system.restaurants.model;

import java.time.LocalTime;

public class OpeningClosingPair {
	private LocalTime opening;
	private LocalTime closing;

	public OpeningClosingPair(LocalTime opening, LocalTime closing) {
		this.opening = opening;
		this.closing = closing;
	}

	public LocalTime getOpening() {
		return opening;
	}

	public void setOpening(LocalTime opening) {
		this.opening = opening;
	}

	public LocalTime getClosing() {
		return closing;
	}

	public void setClosing(LocalTime closing) {
		this.closing = closing;
	}
}
