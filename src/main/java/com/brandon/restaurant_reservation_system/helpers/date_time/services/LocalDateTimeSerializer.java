package com.brandon.restaurant_reservation_system.helpers.date_time.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	@Override
	public void serialize(LocalDateTime localDateTime,
	                      JsonGenerator generator,
	                      SerializerProvider provider) throws IOException {
		try {
			String s = localDateTime.format(GlobalVariables.getDateTimeFormat());
			generator.writeString(s);
		} catch (DateTimeParseException ex) {
//			System.err.println(ex);
			generator.writeString("");
		}
	}
}
