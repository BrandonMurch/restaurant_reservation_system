/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
class TableRepositoryTest {

    @Autowired
    private TableRepository tableRepository;

    RestaurantTable table;
    CombinationOfTables combination;

    @BeforeEach
    void setUp() {
        table = CreateTableForTest.getTable1();
        tableRepository.save(table);

        combination = CreateTableForTest.getCombination1();
        tableRepository.save(combination);

    }

    @Test
    void sanityTest() {
        assertEquals(table, tableRepository.findById(table.getName()).get());
        assertEquals(combination, tableRepository.findById(combination.getName()).get());
    }


    @Test
    void deleteByName() {
        tableRepository.deleteByName(table.getName());
        assertEquals(Optional.empty(), tableRepository.findById(table.getName()));
    }

    @Test
    void deleteWithAssociatedCombinationsDeletesTable() {
        tableRepository.deleteByName(table.getName());
        assertEquals(Optional.empty(), tableRepository.findById(table.getName()));
    }

    @Test
    void deleteWithAssociatedCombinationsDeletesCombination() {
        tableRepository.deleteByName(combination.getName());
        assertEquals(Optional.empty(), tableRepository.findById(combination.getName()));
    }

    @Test
    void findCombinationByName() {
        assertTrue(tableRepository.findById(combination.getName()).isPresent());
    }

    @Test
    void getLargestTableSize() {
        assertEquals(combination.getSeats(), tableRepository.getLargestTableSize());
    }

    @Test
    void findAssociatedCombinations() {
        assertEquals(Collections.singletonList(combination),
          tableRepository.findAssociatedCombinations(table));
    }

    @Test
    void findAllCombinations() {
        assertEquals(1, tableRepository.findAllCombinations().size());
    }
}