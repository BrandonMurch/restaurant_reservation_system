/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.DateRange;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class RestaurantCacheTest {

    @Mock
    private TableAllocatorService tableAllocatorService;
    @Mock
    private Restaurant restaurant;
    @InjectMocks
    private final RestaurantCache cache = new RestaurantCache();

    final SortedSet<LocalDate> availableDates = new TreeSet<>();

    @BeforeEach
    void setUp() {
        availableDates.add(LocalDate.now());
        availableDates.add(LocalDate.now().plusDays(1));
    }

    @Test
    void getAvailableDates() {
        mockInstanceVariables();
        assertEquals(availableDates, cache.getAvailableDates());
    }

    @Test
    void addAvailableDate() {
        mockInstanceVariables();
        RestaurantCache spy = spy(cache);
        Mockito.doNothing().when(spy).checkCache();
        LocalDate twoDays = LocalDate.now().plusDays(2);
        availableDates.add(twoDays);
        spy.addAvailableDate(twoDays);
        assertEquals(availableDates, spy.getAvailableDates());
    }

    @Test
    void removeDateIfUnavailable() {
        mockInstanceVariables();
        Mockito
          .when(restaurant.getBookingTimes(any(LocalDate.class)))
          .thenReturn(Collections.singletonList(LocalTime.now()));
        Mockito
          .when(tableAllocatorService.getAvailableTable(any(LocalDateTime.class), eq(2),
            eq(false)))
          .thenReturn(Collections.emptyList());
        LocalDate oneDay = LocalDate.now().plusDays(1);
        availableDates.remove(oneDay);
        cache.removeDateIfUnavailable(oneDay);
        assertEquals(availableDates, cache.getAvailableDates());
    }

    @Test
    void createCache() {
        Mockito
          .when(restaurant.getBookingDateRange())
          .thenReturn(new DateRange(LocalDate.now(), LocalDate.now().plusDays(2)));
        Mockito
          .when(restaurant.isOpenOnDate(any(LocalDate.class)))
          .thenReturn(true);
        Mockito
          .when(restaurant.getBookingTimes(any(LocalDate.class)))
          .thenReturn(Collections.singletonList(LocalTime.now()));
        Mockito
          .when(tableAllocatorService.getAvailableTable(any(LocalDateTime.class), eq(2),
            eq(false)))
          .thenReturn(Collections.singletonList(new RestaurantTable()));

        SortedSet<LocalDate> expected = new TreeSet<>();
        for (int i = 0; i < 3; i++) {
            expected.add(LocalDate.now().plusDays(i));
        }
        assertEquals(expected, cache.getAvailableDates());
    }

    private void mockInstanceVariables() {
        ReflectionTestUtils.setField(cache, "availableDates", availableDates);
        ReflectionTestUtils.setField(cache, "dateThatDatesLastUpdated", LocalDate.now());
    }
}