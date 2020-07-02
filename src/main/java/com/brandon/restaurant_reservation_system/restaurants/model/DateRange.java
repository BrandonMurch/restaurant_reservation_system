package com.brandon.restaurant_reservation_system.restaurants.model;

import java.time.LocalDate;

public class DateRange {

	private LocalDate reservationStartDate;
	private LocalDate reservationEndDate;

	public DateRange(LocalDate start, LocalDate end) {
		this.reservationStartDate = start;
		this.reservationEndDate = end;
	}

	public LocalDate getReservationStartDate() {
		return reservationStartDate;
	}

	public void setReservationStartDate(LocalDate reservationStartDate) {
		this.reservationStartDate = reservationStartDate;
	}

	public LocalDate getReservationEndDate() {
		return reservationEndDate;
	}

	public void setReservationEndDate(LocalDate reservationEndDate) {
		this.reservationEndDate = reservationEndDate;
	}
}
