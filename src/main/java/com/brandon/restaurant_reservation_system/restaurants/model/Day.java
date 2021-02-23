/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@JsonSerialize(using = DaySerializer.class)
public class Day implements Serializable {

  private static final long serialVersionUID = -1607064466422474736L;
  // instance variables
  protected final DayOfWeek dayOfWeek;
  private final List<DateTimePair> hoursOfOperation;

  //constructors
  public Day(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
    hoursOfOperation = new ArrayList<>();
  }

  public Day(DayOfWeek dayOfWeek,
      List<DateTimePair> hoursOfOperation) {
    this.dayOfWeek = dayOfWeek;
    this.hoursOfOperation = new ArrayList<>(hoursOfOperation);
  }

  public boolean isOpen() {
    return hoursOfOperation.size() != 0;
  }

  public List<DateTimePair> getOpeningPairs() {
    return hoursOfOperation;
  }

  public void addOpeningAndClosing(LocalTime opening, LocalTime closing) {
    this.hoursOfOperation.add(new DateTimePair(opening, closing));
  }

  public void removeOpeningAndClosing(LocalTime opening, LocalTime closing) {
    hoursOfOperation.remove(new DateTimePair(opening, closing));
    Iterator<DateTimePair> itr = this.hoursOfOperation.iterator();
    while (itr.hasNext()) {
      DateTimePair nextPair = itr.next();
      if (opening.equals(nextPair.getOpening())
          && closing.equals(nextPair.getClosing())) {
        itr.remove();
        break;
      }
    }
  }

  public Optional<DateTimePair> getPairThatContainsTime(LocalTime time) {
    for (DateTimePair pair : this.hoursOfOperation) {
      if (time.isAfter(pair.getOpening()) && time.isBefore(pair.getClosing())) {
        return Optional.of(pair);
      }
    }
    return Optional.empty();

  }


  @Override
  public String toString() {
    return dayOfWeek + ": " + (isOpen() ? "Open" : "Closed");
  }
}

class DaySerializer extends StdSerializer<Day> {

  private static final long serialVersionUID = 5192586673879566678L;

  public DaySerializer() {
    this(null);
  }

  public DaySerializer(Class<Day> t) {
    super(t);
  }

  @Override
  public void serialize(
      Day value, JsonGenerator generator, SerializerProvider provider
  ) throws IOException {
    generator.writeStartArray();
    for (var set : value.getOpeningPairs()) {
      generator.writeString(set.getOpening() + " - " + set.getClosing());
    }
    generator.writeEndArray();
  }
}
