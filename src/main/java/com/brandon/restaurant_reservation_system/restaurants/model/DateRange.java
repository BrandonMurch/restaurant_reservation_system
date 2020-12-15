package com.brandon.restaurant_reservation_system.restaurants.model;

import java.io.Serializable;
import java.time.LocalDate;

public class DateRange implements Serializable {

  private static final long serialVersionUID = -723804918732788990L;
  private LocalDate start;
  private LocalDate end;

  public DateRange(LocalDate start, LocalDate end) {
    this.start = start;
    this.end = end;
  }

  public LocalDate getStart() {
    return start;
  }

  public void setStartDate(LocalDate reservationStartDate) {
    this.start = reservationStartDate;
  }

  public LocalDate getEnd() {
    return end;
  }

  public void setEndDate(LocalDate reservationEndDate) {
    this.end = reservationEndDate;
  }
}
