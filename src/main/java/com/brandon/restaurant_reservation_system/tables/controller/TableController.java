/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.tables.controller;

import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.tables.service.TableService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TableController {

  @Autowired
  private TableService tableService;

  @GetMapping(value = "/largest-table")
  public ResponseEntity<?> getTableSizes() {
    return new ResponseEntity<>(tableService.getLargestTableSize(), HttpStatus.OK);
  }

  @GetMapping(value = "/tables")
  public ResponseEntity<?> getAllTables() {

    return new ResponseEntity<>(tableService.findAll(), HttpStatus.OK);
  }

  @PostMapping(value = "/tables")
  public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
    tableService.add(table);
    return buildUriFromTable(table);
  }

  @PostMapping(value = "/combinations")
  public ResponseEntity<?> createCombination(@RequestBody String tables) {
    RestaurantTable created = tableService.createCombination(tables);
    return buildUriFromTable(created);
  }

  @PutMapping(value = "/tables")
  public ResponseEntity<?> updateTablePriorities(@RequestBody List<RestaurantTable> updatedTables) {
    tableService.updateAll(updatedTables);
    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/tables/{name}")
  public ResponseEntity<?> updateTable(@RequestBody RestaurantTable newTable,
      @PathVariable String name) {
    tableService.update(name, newTable);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/tables/{name}")
  public ResponseEntity<?> deleteTable(@PathVariable String name) {
    tableService.remove(name);
    return ResponseEntity.noContent().build();
  }

  private ResponseEntity<String> buildUriFromTable(RestaurantTable table) {
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .replacePath("/tables")
        .path("/{id}")
        .buildAndExpand(table.getName())
        .toUri();
    return ResponseEntity.created(location).build();
  }

}
