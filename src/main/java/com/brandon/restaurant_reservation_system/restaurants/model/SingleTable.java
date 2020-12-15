/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import java.util.Collections;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "single_table")
@DiscriminatorValue("0")

public class SingleTable extends RestaurantTable {

  public SingleTable(String name, int seats, int priority) {
    super(name, seats, priority);
  }

  @Override
  public List<RestaurantTable> getAssociatedTables() {
    return Collections.singletonList(this);
  }

  @Override
  public void removeAssociatedTables() {
  }
}
