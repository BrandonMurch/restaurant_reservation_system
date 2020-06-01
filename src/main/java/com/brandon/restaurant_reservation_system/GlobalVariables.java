package com.brandon.restaurant_reservation_system;

import java.time.format.DateTimeFormatter;

public final class GlobalVariables {

	private final static DateTimeFormatter timeFormat =
			DateTimeFormatter.ISO_LOCAL_TIME;
	private final static DateTimeFormatter dateTimeFormat =
			DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private final static DateTimeFormatter dateFormat =
			DateTimeFormatter.ISO_LOCAL_DATE;

	private GlobalVariables() {}

	public static DateTimeFormatter getTimeFormat() {
		return timeFormat;
	}

	public static DateTimeFormatter getDateTimeFormat() {
		return dateTimeFormat;
	}

	public static DateTimeFormatter getDateFormat() {
		return dateFormat;
	}
}
