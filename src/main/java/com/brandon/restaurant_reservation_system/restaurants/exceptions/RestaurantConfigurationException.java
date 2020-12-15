package com.brandon.restaurant_reservation_system.restaurants.exceptions;

public class RestaurantConfigurationException extends RuntimeException {

  private static final long serialVersionUID = -4997584171121456402L;

  public RestaurantConfigurationException(String message) {
    super("Restaurant " + message + " have not been configured correctly.");
  }
}
