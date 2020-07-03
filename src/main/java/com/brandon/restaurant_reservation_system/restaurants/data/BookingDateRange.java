package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;

import java.time.LocalDate;

public class BookingDateRange {
	private boolean fixedBookingWindow;
	private int bookingHorizonInDays;
	private DateRange dateRange;

	public BookingDateRange(int bookingHorizonInDays) {
		setBookingRange(bookingHorizonInDays);
	}

	public BookingDateRange(
			DateRange bookingDateRange) {
		setBookingRange(bookingDateRange);
	}

	public DateRange getBookingRange() {
		if (fixedBookingWindow) {
			return dateRange;
		}
		LocalDate today = LocalDate.now();
		LocalDate end = today.plusDays(bookingHorizonInDays);
		return new DateRange(today, end);
	}

	public void setBookingRange(int bookingHorizonInDays) {
		this.fixedBookingWindow = false;
		this.bookingHorizonInDays = bookingHorizonInDays;
	}

	public void setBookingRange(DateRange bookingDateRange) {
		this.fixedBookingWindow = true;
		this.dateRange = bookingDateRange;
	}
}
