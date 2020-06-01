package com.brandon.restaurant_reservation_system.helpers.date_time.services;

import com.brandon.restaurant_reservation_system.helpers.date_time.exceptions.DateTimeFormatException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeHandler {



	// DATE
	public static String formatDate(LocalDate date,
	                                DateTimeFormatter formatter) {
		return date.format(formatter);
	}

	public static String formatDate(LocalDate date,
	                                CustomDateTimeFormatter dateType) {
		return date.format(DateTimeFormatter.ofPattern(dateType.value()));
	}

	public static LocalDateTime parseDateAsDateTime(String date,
	                                                CustomDateTimeFormatter dateType) {
		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern(dateType.value());
		return parseDateAsDateTime(date, formatter);
	}

	public static LocalDateTime parseDateAsDateTime(String date,
	                                                DateTimeFormatter formatter) {
		return LocalDate.parse(date, formatter).atStartOfDay();
	}

	public static boolean areDatesEqual(LocalDateTime date1,
	                                    LocalDateTime date2) {
		return LocalDate.from(date1).equals(LocalDate.from(date2));
	}

	// TIME
	public static LocalTime parseTime(String date,
	                                  DateTimeFormatter formatter) {
		try {
			return LocalTime.parse(date, formatter);
		} catch (DateTimeParseException ex) {
			throw new DateTimeFormatException(date, formatter);
		}
	}


	// DATE TIME
	public static String formatDateTime(LocalDateTime date,
	                                    DateTimeFormatter formatter) {
		return date.format(formatter);
	}

	public static String formatDateTime(LocalDateTime date,
	                                    CustomDateTimeFormatter customDateTimeFormatter) {
		return date.format(
				DateTimeFormatter.ofPattern(customDateTimeFormatter.value()));
	}

	public static LocalDateTime parseDateTime(String date,
	                                          CustomDateTimeFormatter dateType) {
		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern(dateType.value());
		return parseDateTime(date, formatter);
	}

	public static LocalDateTime parseDateTime(String date,
	                                          DateTimeFormatter formatter) {
		try {
			return LocalDateTime.parse(date, formatter);
		} catch (DateTimeParseException ex) {
			throw new DateTimeFormatException(date, formatter);
		}
	}


	public static LocalDate parseDate(String date, DateTimeFormatter dateFormat) {
			try {
				return LocalDate.parse(date, dateFormat);
			} catch (DateTimeParseException ex) {
				throw new DateTimeFormatException(date, dateFormat);
			}
	}
}
