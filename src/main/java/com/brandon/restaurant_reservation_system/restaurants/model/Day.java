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
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonSerialize(using = DaySerializer.class)
public class Day implements Serializable {

  private static final long serialVersionUID = -1607064466422474736L;
  protected final DayOfWeek dayOfWeek;
  private List<TimePair> hoursOfOperation;
  private List<LocalTime> bookingTimes = new ArrayList<>();
  private boolean areBookingTimesSetByInterval = true;

  private Day(DayOfWeek dayOfWeek,
      List<TimePair> hoursOfOperation) {
    this.dayOfWeek = dayOfWeek;
    this.hoursOfOperation = new ArrayList<>(hoursOfOperation);
  }

  public static Day createDay(DayOfWeek dayOfWeek,
      List<TimePair> hoursOfOperation, Integer intervalInMinutes) {
    if (hoursOfOperation == null) {
      hoursOfOperation = new ArrayList<>();
    }
    Day day = new Day(dayOfWeek, hoursOfOperation);
    day.calculateBookingTimes(intervalInMinutes);
    return day;
  }

  public List<LocalTime> getBookingTimes() {
    return bookingTimes;
  }

  public void setBookingTimes(List<LocalTime> bookingTimes) {
    this.bookingTimes = bookingTimes;
    areBookingTimesSetByInterval = false;
  }

  protected boolean areBookingTimesSetByInterval() {
    return areBookingTimesSetByInterval;
  }

  public boolean isOpen() {
    return hoursOfOperation.size() != 0;
  }

  public boolean isOpen(LocalTime time) {
    return bookingTimes.contains(time);
  }

  public List<TimePair> getOpeningPairs() {
    return hoursOfOperation;
  }

  public void setOpeningHours(List<TimePair> timePairs) {
    this.hoursOfOperation = timePairs;
  }

  public void addOpeningAndClosing(LocalTime opening, LocalTime closing) {
    this.hoursOfOperation.add(new TimePair(opening, closing));
  }

  public void removeOpeningAndClosing(LocalTime opening, LocalTime closing) {
    hoursOfOperation.remove(new TimePair(opening, closing));
    Iterator<TimePair> itr = this.hoursOfOperation.iterator();
    while (itr.hasNext()) {
      TimePair nextPair = itr.next();
      if (opening.equals(nextPair.getOpening())
          && closing.equals(nextPair.getClosing())) {
        itr.remove();
        break;
      }
    }
  }

  public Optional<TimePair> getPairThatContainsTime(LocalTime time) {
    for (TimePair pair : this.hoursOfOperation) {
      if (time.isAfter(pair.getOpening()) && time.isBefore(pair.getClosing())) {
        return Optional.of(pair);
      }
    }
    return Optional.empty();
  }

  public void setBookingSlotInterval(int intervalInMinutes) {
    if (intervalInMinutes > 0) {
      calculateBookingTimes(intervalInMinutes);
    } else {
      bookingTimes = new ArrayList<>();
    }
    areBookingTimesSetByInterval = true;
  }

  private void calculateBookingTimes(int intervalInMinutes) {
    this.bookingTimes = new ArrayList<>();
    hoursOfOperation.forEach((hourSet) -> {
      LocalTime currentTime = hourSet.getOpening();
      while (currentTime.isBefore(hourSet.getClosing())) {
        this.bookingTimes.add(currentTime);
        currentTime = currentTime.plusMinutes(intervalInMinutes);
      }
    });
  }

  protected String bookingTimesOrIntervalForJSON() {
    if (!isOpen()) {
      return "";
    }

    if (areBookingTimesSetByInterval) {
      return String.valueOf(
          Duration.between(bookingTimes.get(0), bookingTimes.get(1))
              .toMinutes()
      );
    }
    return Arrays.stream(bookingTimes.toArray()).map(Object::toString)
        .collect(Collectors.joining(", "));
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
    generator.writeStartObject();
    generator.writeFieldName("openingHours");
    generator.writeStartArray();
    for (var set : value.getOpeningPairs()) {
      generator.writeString(set.getOpening() + " - " + set.getClosing());
    }
    generator.writeEndArray();
    generator.writeStringField(
        "bookingTimeType",
        value.areBookingTimesSetByInterval() ? "INTERVAL" : "BOOKING_TIMES"
    );
    generator.writeStringField(
        "bookingTimes",
        value.bookingTimesOrIntervalForJSON());
    generator.writeEndObject();
  }
}



