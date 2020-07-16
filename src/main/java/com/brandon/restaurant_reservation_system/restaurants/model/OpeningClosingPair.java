/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

public class OpeningClosingPair implements Serializable {
	private static final long serialVersionUID = -296054023821005011L;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OpeningClosingPair that = (OpeningClosingPair) o;
		return Objects.equals(getOpening(), that.getOpening()) &&
		Objects.equals(getClosing(), that.getClosing());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOpening(), getClosing());
	}
}
