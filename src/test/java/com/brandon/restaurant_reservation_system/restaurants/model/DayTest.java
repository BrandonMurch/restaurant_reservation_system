/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DayTest {

  final LocalTime now = LocalTime.now();
  final LocalTime oneHour = now.plusHours(1);
  final OpeningClosingPair pair = new OpeningClosingPair(now, oneHour);
  private Day day;

  @BeforeEach
  void setup() {
    day = new Day(DayOfWeek.WEDNESDAY, Collections.singletonList(pair));
  }

  @Test
  void removeOpeningAndClosing() {
    day.removeOpeningAndClosing(now, oneHour);
    assertTrue(day.getOpeningPairs().isEmpty());
  }

  @Test
  void getPairThatContainsTime() {
    Optional<OpeningClosingPair> optionalPair =
        day.getPairThatContainsTime(now.plusMinutes(30));
    if (optionalPair.isPresent()) {
      assertEquals(pair, optionalPair.get());
    } else {
      fail();
    }
    optionalPair =
        day.getPairThatContainsTime(now.minusMinutes(30));
    assertEquals(Optional.empty(), optionalPair);

  }
}