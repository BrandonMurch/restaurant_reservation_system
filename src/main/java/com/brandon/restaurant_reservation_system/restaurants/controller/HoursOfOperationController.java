/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
import com.brandon.restaurant_reservation_system.restaurants.model.TimePair;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hours-of-operation")
public class HoursOfOperationController {

  @Autowired
  private HoursOfOperation hoursOfOperation;

  @GetMapping()
  public ResponseEntity<?> getHours() {
    return ResponseEntity.ok(hoursOfOperation.get());
  }

  @PutMapping("/interval/{day}")
  public ResponseEntity<?> updateInterval(@PathVariable String day, @RequestBody int interval) {
    hoursOfOperation.setTimes(DayOfWeek.valueOf(day.toUpperCase()), interval);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/booking-times/{day}")
  public ResponseEntity<?> updateBookingTimes(@PathVariable String day,
      @RequestBody List<LocalTime> times) {
    hoursOfOperation.setTimes(DayOfWeek.valueOf(day.toUpperCase()), times);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/opening-times/{day}")
  public ResponseEntity<?> updateOpeningHours(@PathVariable String day,
      @RequestBody List<String> times) {
    List<TimePair> timePairs = parseTimePairStrings(times);
    hoursOfOperation.setOpeningHours(DayOfWeek.valueOf(day.toUpperCase()), timePairs);
    return ResponseEntity.noContent().build();
  }

  @PutMapping()
  public ResponseEntity<?> updateHours(@RequestBody Map<String, List<String>> newHours) {
    hoursOfOperation.set(convertToHoursOfOperation(newHours));
    return ResponseEntity.noContent().build();
  }


  public Map<DayOfWeek, Day> convertToHoursOfOperation(Map<String, List<String>> hours) {
    Map<DayOfWeek, Day> convertedHours = new HashMap<>();
    for (Entry<String, List<String>> entry : hours.entrySet()) {
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(entry.getKey().toUpperCase());
      List<TimePair> timePairs = parseTimePairStrings(entry.getValue());
      convertedHours.put(dayOfWeek, Day.createDay(dayOfWeek, timePairs, 0));
    }
    return convertedHours;
  }

  private List<TimePair> parseTimePairStrings(List<String> timeStrings) {
    List<TimePair> timePairs = new ArrayList<>();
    for (String pairString : timeStrings) {
      var timePair = pairString.split(" - ");
      timePairs.add(
          new TimePair(
              LocalTime.parse(timePair[0]),
              LocalTime.parse(timePair[1])
          )
      );
    }
    return timePairs;
  }
}

