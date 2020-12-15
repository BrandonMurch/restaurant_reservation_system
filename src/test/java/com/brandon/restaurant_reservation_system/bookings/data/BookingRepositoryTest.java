/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
class BookingRepositoryTest {

  private final CreateBookingsForTest createBooking = new CreateBookingsForTest();
  Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    User user = booking.getUser();
    userRepository.save(user);
    Optional<User> optUser = userRepository.findByEmail(user.getEmail());
    if (optUser.isPresent()) {
      user = optUser.get();
    }
    booking.setUser(user);
    booking = bookingRepository.save(booking);
  }

  @Test
  void getBookingsDuringTime() {
    List<Booking> result = bookingRepository
        .getBookingsDuringTime(booking.getStartTime(), booking.getEndTime());
    assertEquals(1, result.size());
    assertEquals(booking, result.get(0));
  }

  @Test
  void getBookingsByStartTime() {
    List<Booking> result = bookingRepository
        .getBookingsByStartTime(booking.getStartTime());
    assertEquals(1, result.size());
    assertEquals(booking, result.get(0));
  }

  @Test
  void getBookingsBetweenDates() {
    LocalDateTime date = booking.getStartTime().toLocalDate().atStartOfDay();
    LocalDateTime nextDate = date.plusDays(1);
    List<Booking> result = bookingRepository
        .getBookingsBetweenDates(date, nextDate);
    assertEquals(1, result.size());
    assertEquals(booking, result.get(0));
  }

  @Test
  void getBookingsByUser() {
    List<Booking> result =
        bookingRepository.getBookingsByUser(booking.getUser().getEmail());
    assertEquals(1, result.size());
    assertEquals(booking, result.get(0));
  }

  @Test
  void getCountByDay() {
    List<Booking> allBookings = bookingRepository.findAll();
    LocalDate date = LocalDate.parse("2020-12-11");
    int count = 0;
    for (Booking booking : allBookings) {
      if (booking.getDate().isEqual(date)) {
        count += booking.getPartySize();
      }
    }
    HashMap<LocalDate, Integer> results = bookingRepository.getCountByDayMap();
    assertEquals(count, results.get(LocalDate.parse("2020-12-11")));
  }
}