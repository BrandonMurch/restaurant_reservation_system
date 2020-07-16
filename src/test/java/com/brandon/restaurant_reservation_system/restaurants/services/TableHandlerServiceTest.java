/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.CombinationRepository;
import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TableHandlerServiceTest {

    @Mock
    private TableRepository tableRepository;
    @Mock
    private CombinationRepository combinationRepository;
    @InjectMocks
    private TableHandlerService tableHandlerService;

    private final List<RestaurantTable> tableList = Arrays.asList(
      new RestaurantTable("1", 4),
      new RestaurantTable("5", 4),
      new RestaurantTable("20", 2),
      new RestaurantTable("21", 2),
      new RestaurantTable("22", 2)
    );

    private final List<CombinationOfTables> comboList = Arrays.asList(
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("21", 2),
        new RestaurantTable("22", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("20", 2),
        new RestaurantTable("21", 2),
        new RestaurantTable("22", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("21", 2),
        new RestaurantTable("22", 2)
      )),
      new CombinationOfTables(Arrays.asList(
        new RestaurantTable("1", 4),
        new RestaurantTable("5", 4)
      ))
    );

    @Test
    void updateLargestTableSize() {
        Mockito
          .when(tableRepository.findAll())
          .thenReturn(tableList);
        Mockito
          .when(combinationRepository.findAll())
          .thenReturn(comboList);
        tableHandlerService.updateLargestTableSize();

        assertEquals(8, tableHandlerService.getLargestTableSize());
    }
}