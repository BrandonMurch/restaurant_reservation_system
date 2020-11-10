/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.data.TableRepository;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TableHandlerServiceTest {

    @Mock
    private TableRepository tableRepository;
    @InjectMocks
    private TableHandlerService tableHandlerService;

    RestaurantTable table1 = new RestaurantTable("1", 4, 1);
    RestaurantTable table5 = new RestaurantTable("5", 4, 2);
    RestaurantTable table20 = new RestaurantTable("20", 2, 3);
    RestaurantTable table21 = new RestaurantTable("21", 2, 4);
    RestaurantTable table22 = new RestaurantTable("22", 2, 5);


    private final List<RestaurantTable> tableList = Arrays.asList(
      table1, table5, table20, table21, table22
    );

    private final List<CombinationOfTables> comboList = Arrays.asList(
      new CombinationOfTables(Arrays.asList(
        table20, table21
      ), 6),
      new CombinationOfTables(Arrays.asList(
        table20, table21, table22
      ), 7),
      new CombinationOfTables(Arrays.asList(
        table21, table22
      ), 8),
      new CombinationOfTables(Arrays.asList(
        table1, table5
      ), 9)
    );

}