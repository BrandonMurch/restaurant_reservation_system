/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;


import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantCache {

  private final SortedSet<LocalDate> availableDates = new TreeSet<>();
  @Autowired
  private Restaurant restaurant;
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private TableAllocatorService tableAllocatorService;
  private LocalDate datesLastUpdated;
  private LocalDate countsLastUpdated;
  private Map<LocalDate, Integer> bookingsPerDate = new HashMap<>();


  public RestaurantCache() {
  }

  public Map<LocalDate, Integer> getBookingsPerDate() {
    didCountsNeedToBeUpdated();
    return bookingsPerDate;
  }

  public void addBookingToDate(LocalDate date, Integer numberOfBookings) {
    boolean didCacheUpdate = didCountsNeedToBeUpdated();
    if (!didCacheUpdate) {
      bookingsPerDate.merge(date, numberOfBookings, Integer::sum);
    }
  }

  private Integer getDifference(Integer value1, Integer value2) {
    return value1 - value2;
  }

  public void removeBookingFromDate(LocalDate date, Integer numberOfBookings) {
    boolean didCacheUpdate = didCountsNeedToBeUpdated();
    if (!didCacheUpdate) {
      bookingsPerDate.merge(date, numberOfBookings, this::getDifference);
    }
  }

  protected boolean didCountsNeedToBeUpdated() {
    if (bookingsPerDate == null
        || bookingsPerDate.isEmpty()
        || !countsLastUpdated.isEqual(LocalDate.now())) {
      createBookingsPerDate();
      return true;
    }
    return false;
  }

  protected void createBookingsPerDate() {
    bookingsPerDate = bookingRepository.getCountByDayMap();
    countsLastUpdated = LocalDate.now();
  }

  public SortedSet<LocalDate> getAvailableDates() {
    checkAvailableDatesCache();
    return availableDates;
  }

  public void addAvailableDate(LocalDate date) {
    checkAvailableDatesCache();
    availableDates.add(date);
  }

  public void removeDateIfUnavailable(LocalDate date) {
    checkAvailableDatesCache();
    if (!tryBookingOnDate(date)) {
      availableDates.remove(date);
    }
  }

  protected void checkAvailableDatesCache() {
    if (availableDates.isEmpty()
        || !datesLastUpdated.isEqual(LocalDate.now())) {
      createAvailableDatesCache();
    }
  }

  protected void createAvailableDatesCache() {
    DateRange dates = restaurant.getBookingDateRange();
    LocalDate current = dates.getStart();
    LocalDate end = dates.getEnd().plusDays(1);

    while (current.isBefore(end)) {
      if (restaurant.isOpenOnDate(current)
          && tryBookingOnDate(current)) {
        availableDates.add(current);
      }
      current = current.plusDays(1);
    }
    datesLastUpdated = LocalDate.now();
  }

  private boolean tryBookingOnDate(LocalDate date) {
    List<LocalTime> times = restaurant.getBookingTimes(date);
    for (LocalTime time : times) {
      LocalDateTime dateTime = date.atTime(time);

      if (!tableAllocatorService.getAvailableTable(dateTime, 2,
          false).isEmpty()) {
        return true;
      }
    }
    return false;
  }
}

