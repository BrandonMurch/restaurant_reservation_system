/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RestaurantCacheTest {

    @Mock
    private TableAllocatorService tableAllocatorService;
    @InjectMocks
    private final RestaurantCache cache = new RestaurantCache();

    SortedSet<LocalDate> availableDates = new TreeSet<>();

    @BeforeEach
    void setUp() {
        availableDates.add(LocalDate.now());
        availableDates.add(LocalDate.now().plusDays(1));
    }

    @Test
    void getAvailableDates() {
        Mockito
          .when(tableAllocatorService.getAvailableDates())
          .thenReturn(availableDates);
        assertEquals(availableDates, cache.getAvailableDates());
    }

    @Test
    void addAvailableDate() {
        Mockito
          .when(tableAllocatorService.getAvailableDates())
          .thenReturn(availableDates);
        LocalDate twoDays = LocalDate.now().plusDays(2);
        availableDates.add(twoDays);
        cache.addAvailableDate(twoDays);
        assertEquals(availableDates, cache.getAvailableDates());
    }

    @Test
    void removeAvailableDate() {
        Mockito
          .when(tableAllocatorService.getAvailableDates())
          .thenReturn(availableDates);
        LocalDate oneDay = LocalDate.now().plusDays(1);
        availableDates.add(oneDay);
        cache.addAvailableDate(oneDay);
        assertEquals(availableDates, cache.getAvailableDates());
    }

    @Test
    void setAvailableDates() {
        LocalDate twoDays = LocalDate.now().plusDays(2);
        availableDates.add(twoDays);
        cache.setAvailableDates(availableDates);
        assertEquals(availableDates, cache.getAvailableDates());
    }
}