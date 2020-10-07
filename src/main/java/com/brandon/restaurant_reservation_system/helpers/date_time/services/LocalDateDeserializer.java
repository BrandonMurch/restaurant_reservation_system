/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.date_time.services;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer extends
  JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctx)
      throws IOException {
        String str = p.getText();
        try {
            return LocalDate.parse(str, GlobalVariables.getDateFormat());
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(str);
        } catch (DateTimeParseException ignored) {
        }

        return null;
    }
}