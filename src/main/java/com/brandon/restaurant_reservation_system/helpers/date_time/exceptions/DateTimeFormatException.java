package com.brandon.restaurant_reservation_system.helpers.date_time.exceptions;

import com.brandon.restaurant_reservation_system.helpers.date_time.services.CustomDateTimeFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatException extends RuntimeException {

	public DateTimeFormatException(String dateInput, CustomDateTimeFormatter dateFormat) {
		super("Date entered was " + dateInput + ", format must be "
				+ dateFormat.getExample());
	}

	public DateTimeFormatException(String dateInput, DateTimeFormatter dateFormat) {
		super("Date entered was " + dateInput + ", format must be "
				+ LocalDateTime.now().format(dateFormat));
	}

	public DateTimeFormatException(DateTimeFormatter dateFormat) {
		super("The format for Datetimes must be "
				+ LocalDateTime.now().format(dateFormat));
	}

    private static final long serialVersionUID = -8803453916392107435L;
}
