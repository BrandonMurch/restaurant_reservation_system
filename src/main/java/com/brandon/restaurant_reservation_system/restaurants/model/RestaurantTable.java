/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import java.util.List;
import java.util.Objects;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name = "restaurant_table")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "combination", discriminatorType =
    DiscriminatorType.INTEGER, columnDefinition = "TINYINT(1)")
public abstract class RestaurantTable {

  @Id
  private String name;
  private int seats;
  private int priority;

  public RestaurantTable() {
  }

  public RestaurantTable(String name, int seats, int priority) {
    this.name = name;
    this.seats = seats;
    this.priority = priority;
  }

  public String getName() {
    return this.name;
  }

  protected void setName(String name) {
    this.name = name.isEmpty() ? this.name : name;
  }

  public int getSeats() {
    return this.seats;
  }

  protected void setSeats(int seats) {
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

  public abstract List<RestaurantTable> getAssociatedTables();

  public abstract void removeAssociatedTables();

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
