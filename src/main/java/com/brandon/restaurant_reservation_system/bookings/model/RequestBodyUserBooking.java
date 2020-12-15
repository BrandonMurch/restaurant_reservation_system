package com.brandon.restaurant_reservation_system.bookings.model;

import com.brandon.restaurant_reservation_system.users.model.User;

public class RequestBodyUserBooking {

  private User user;
  private Booking booking;

  public RequestBodyUserBooking() {
  }

  public RequestBodyUserBooking(
      User user,
      Booking booking) {
    this.user = user;
    this.booking = booking;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(
      Booking booking) {
    this.booking = booking;
  }
}
