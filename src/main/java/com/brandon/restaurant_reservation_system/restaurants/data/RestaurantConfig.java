/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.data.Updatable;
import com.brandon.restaurant_reservation_system.restaurants.services.PopulateRestaurantService;
import java.io.Serializable;
import java.time.Duration;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantConfig implements Serializable, Updatable {

  private static final long serialVersionUID = 7979825971594010456L;
  private int capacity;
  private boolean limitPeoplePerInterval;
  private int peoplePerInterval;
  private boolean canABookingOccupyALargerTable;
  private Duration standardBookingDuration;
  @Autowired
  private PopulateRestaurantService populateRestaurant;

  public RestaurantConfig() {
    capacity = 0;
    limitPeoplePerInterval = false;
    peoplePerInterval = 0;
    canABookingOccupyALargerTable = false;
    standardBookingDuration = Duration.ZERO;
  }

  @PostConstruct
  private void postConstruct() {
    // TODO: reinstate this in production
    //		boolean isDeserializeSuccess = deserialize();

    //		if (!isDeserializeSuccess) {
    populateRestaurant.populate();
    //		}
    // TODO: Remove this when database is created.
    populateRestaurant.populateTables();
  }


  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public boolean arePeopleLimitedPerInterval() {
    return limitPeoplePerInterval;
  }

  public int getPeoplePerInterval() {
    return peoplePerInterval;
  }

  public void setPeoplePerInterval(int peoplePerInterval) {
    this.limitPeoplePerInterval = peoplePerInterval != 0;
    this.peoplePerInterval = peoplePerInterval;
  }

  public boolean canABookingOccupyALargerTable() {
    return canABookingOccupyALargerTable;
  }

  public Duration getStandardBookingDuration() {
    return standardBookingDuration;
  }

  public void setStandardBookingDuration(int durationInMinutes) {
    this.standardBookingDuration = Duration.ofMinutes(durationInMinutes);

  }

  public void limitPeoplePerTimeInterval(int peoplePerInterval) {
    limitPeoplePerInterval = true;
    this.peoplePerInterval = peoplePerInterval;
  }

  public void noLimitPeoplePerTimeInterval() {
    limitPeoplePerInterval = false;
    this.peoplePerInterval = 0;
  }

  public void setCanABookingOccupyALargerTable(boolean bool) {
    this.canABookingOccupyALargerTable = bool;
  }

  @Override
  public void update(Object object) {
    if (object instanceof RestaurantConfig) {
      RestaurantConfig newConfig = (RestaurantConfig) object;
      this.capacity = newConfig.capacity;
      this.limitPeoplePerInterval = newConfig.limitPeoplePerInterval;
      this.peoplePerInterval = newConfig.peoplePerInterval;
      this.canABookingOccupyALargerTable = newConfig.canABookingOccupyALargerTable;
      this.standardBookingDuration = newConfig.standardBookingDuration;
    }
  }
}
