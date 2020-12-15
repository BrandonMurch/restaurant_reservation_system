package com.brandon.restaurant_reservation_system.helpers.date_time.services;

// Modified from
// https://www.logicbig.com/tutorials/misc/jackson/json-serialize-deserialize.html

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer extends
    JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
      throws IOException {
    String str = p.getText();
    try {
      return LocalDateTime.parse(str, GlobalVariables.getDateTimeFormat());
    } catch (DateTimeParseException ignored) {
    }
    try {
      return LocalDateTime.parse(str);
    } catch (DateTimeParseException ignored) {
    }
    return null;
  }
}