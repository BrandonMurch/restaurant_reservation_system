package com.brandon.restaurant_reservation_system.restaurants.model;

import java.time.LocalDate;

public class DateRange {

	private LocalDate start;
	private LocalDate end;

	public DateRange(LocalDate start, LocalDate end) {
		this.start = start;
		this.end = end;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStartDate(LocalDate reservationStartDate) {
		this.start = reservationStartDate;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEndDate(LocalDate reservationEndDate) {
		this.end = reservationEndDate;
	}
}
