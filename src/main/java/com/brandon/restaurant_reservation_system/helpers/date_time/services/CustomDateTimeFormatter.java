package com.brandon.restaurant_reservation_system.helpers.date_time.services;

public enum CustomDateTimeFormatter {
	DATE ("dd-MM-yyyy", "01-02-2000"),
	TIME_NO_SECONDS ("h:mm a", "1:00 PM"),
	DATE_TIME("dd-MM-yyyy HH:mm", "12-22-2000 14:03"),
	READABLE_DATE("EEEE, MMMM dd yyyy",
			"Wednesday, April 7 2020");

	private final String format;
	private final String example;

	CustomDateTimeFormatter(String format, String example) {
		this.format = format;
		this.example = example;
	}

	public String value() {
		return format;
	}

	public String getExample() {
		return example;
	}
}
