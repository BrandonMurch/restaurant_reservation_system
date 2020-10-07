/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.date_time.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate localDate,
                          JsonGenerator generator,
                          SerializerProvider provider) throws IOException {
        try {
            String s = localDate.format(GlobalVariables.getDateFormat());
            generator.writeString(s);
        } catch (DateTimeParseException ex) {
            generator.writeString("");
        }
    }
}
