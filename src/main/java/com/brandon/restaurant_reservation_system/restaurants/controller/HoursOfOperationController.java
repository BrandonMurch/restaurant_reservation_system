/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import com.brandon.restaurant_reservation_system.restaurants.data.HoursOfOperation;
import com.brandon.restaurant_reservation_system.restaurants.model.DateTimePair;
import com.brandon.restaurant_reservation_system.restaurants.model.Day;
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

  @PutMapping()
  public ResponseEntity<?> updateHours(@RequestBody Map<String, List<String>> newHours) {
    hoursOfOperation.set(convertToHoursOfOperation(newHours));
    return ResponseEntity.noContent().build();
  }


  public Map<DayOfWeek, Day> convertToHoursOfOperation(Map<String, List<String>> hours) {
    Map<DayOfWeek, Day> convertedHours = new HashMap<>();
    for (Entry<String, List<String>> entry : hours.entrySet()) {
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(entry.getKey().toUpperCase());
      List<DateTimePair> timePairs = new ArrayList<>();
      for (String pairString : entry.getValue()) {
        var timePair = pairString.split(" - ");
        timePairs.add(
            new DateTimePair(
                LocalTime.parse(timePair[0]),
                LocalTime.parse(timePair[1])
              )
          );
        }
        convertedHours.put(dayOfWeek, new Day(dayOfWeek, timePairs));
      }
      return convertedHours;
    }
}

