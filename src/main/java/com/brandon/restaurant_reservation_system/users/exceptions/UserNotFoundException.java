package com.brandon.restaurant_reservation_system.users.exceptions;

public class UserNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -5771175479104494740L;

  public UserNotFoundException(long id) {
    super(String.format("User id not found: %d", id));
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}