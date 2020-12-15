/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name = "restaurant_table")
public class RestaurantTable {

  @Id
  private String name;
  private int seats;
  private int priority;
  @ManyToMany(targetEntity = RestaurantTable.class, cascade =
      CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(name = "combination_table",
      joinColumns = @JoinColumn(name = "combination_id")
  )
  private List<RestaurantTable> tables = new ArrayList<>();

  public RestaurantTable() {
  }

  public RestaurantTable(String name, int seats, int priority) {
    this.name = name;
    this.seats = seats;
    this.priority = priority;
  }

  public RestaurantTable(List<RestaurantTable> restaurantTables, int priority) {
    this.tables.addAll(restaurantTables);
    this.seats = calculateSeats(restaurantTables);
    this.name = calculateName(restaurantTables);
    this.priority = priority;
  }

  private int calculateSeats(List<RestaurantTable> restaurantTables) {
    return restaurantTables.stream().reduce(0,
        (previous, current) -> previous + current.getSeats(), Integer::sum);
  }

  private String calculateName(List<RestaurantTable> restaurantTables) {
    return restaurantTables.stream().map(RestaurantTable::getName)
        .collect(Collectors.joining(", "));
  }

  public String getName() {
    return this.name;
  }

  private void setName(String name) {
    this.name = name.isEmpty() ? this.name : name;
  }

  public int getSeats() {
    return this.seats;
  }

  private void setSeats(int seats) {
    this.seats = Math.max(seats, 1);
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = Math.max(priority, 0);
  }

  public void update(RestaurantTable newTable) {
    setName(newTable.name);
    setSeats(newTable.seats);
    setPriority(newTable.priority);
  }

  @JsonIgnore
  public List<RestaurantTable> getTables() {
    if (tables.isEmpty()) {
      return Collections.singletonList(this);
    }
    return tables;
  }

  public void setTables(List<RestaurantTable> tables) {
    this.tables = new ArrayList<>(tables);
  }

  public void removeTables() {
    tables.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestaurantTable that = (RestaurantTable) o;
    return getSeats() == that.getSeats() &&
        Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getSeats());
  }

  @Override
  public String toString() {
    return "RestaurantTable{" +
        "name='" + this.getName() + '\'' +
        ", seats=" + this.getSeats() +
        '}';
  }
}
