/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService.populateRestaurant;
import static com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService.populateRestaurantTables;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PopulateRestaurantServiceTest {

    @Mock
    private TableHandlerService tables;
    @InjectMocks
    private final Restaurant restaurant = new Restaurant();

    private List<RestaurantTable> tableList;
    private List<CombinationOfTables> comboList;

    @BeforeEach
    void setUp() {
        tableList = Arrays.asList(
          new RestaurantTable("k1", 2, 1),
          new RestaurantTable("k2", 2, 1),
          new RestaurantTable("b1", 2, 1),
          new RestaurantTable("b2", 2, 1),
          new RestaurantTable("1", 4, 1),
          new RestaurantTable("5", 4, 1),
          new RestaurantTable("20", 2, 1),
          new RestaurantTable("21", 2, 1),
          new RestaurantTable("22", 2, 1),
          new RestaurantTable("23", 2, 1),
          new RestaurantTable("24", 2, 1),
          new RestaurantTable("25", 2, 1)
        );

        comboList = Arrays.asList(
          new CombinationOfTables(Arrays.asList(
            new RestaurantTable("21", 2, 1),
            new RestaurantTable("22", 2, 1)
          ), 1),
          new CombinationOfTables(Arrays.asList(
            new RestaurantTable("21", 2, 1),
            new RestaurantTable("22", 2, 1),
            new RestaurantTable("23", 2, 1)
          ), 1),
          new CombinationOfTables(Arrays.asList(
            new RestaurantTable("22", 2, 1),
            new RestaurantTable("23", 2, 1)
          ), 1),
          new CombinationOfTables(Arrays.asList(
            new RestaurantTable("1", 4, 1),
            new RestaurantTable("5", 4, 1)
          ), 1)
        );
    }

    @Test
    void populateRestaurantTest() {
        populateRestaurant(restaurant);
        assertEquals(20, restaurant.getCapacity());
        LocalDateTime dateTime = LocalDate.now().atTime(20, 0);
        while (dateTime.getDayOfWeek() != DayOfWeek.SATURDAY) {
            dateTime = dateTime.plusDays(1);
        }
        boolean result = restaurant.isBookingTime(dateTime);
        assertTrue(result);

        dateTime = dateTime.toLocalDate().atTime(9, 0);
        result = restaurant.isBookingTime(dateTime);
        assertFalse(result);

        dateTime = LocalDate.now().atTime(20, 0);
        while (dateTime.getDayOfWeek() != DayOfWeek.MONDAY) {
            dateTime = dateTime.plusDays(1);
        }
        result = restaurant.isBookingTime(dateTime);
        assertFalse(result);
    }

    @Test
    void populateRestaurantTablesTest() {
        Mockito
          .when(tables.getAll())
          .thenReturn(tableList);
        Mockito
          .when(tables.getAllCombinations())
          .thenReturn(comboList);
        populateRestaurant(restaurant);
        populateRestaurantTables(restaurant);

        List<RestaurantTable> tableList = restaurant.getTableList();
        assertEquals(12, tableList.size());

        RestaurantTable table = new RestaurantTable("k1", 2, 1);
        assertTrue(tableList.contains(table));

        table = new RestaurantTable("55989", 2, 1);
        assertFalse(tableList.contains(table));


        List<CombinationOfTables> comboList = restaurant.getAllCombinationsOfTables();
        assertEquals(4, comboList.size());
    }
}