/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.services;

import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotPossibleException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.DuplicateFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.exceptions.TableNotFoundException;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAllocatorService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.brandon.restaurant_reservation_system.users.service.UserService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingService {

  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private TableAllocatorService tableAllocatorService;
  @Autowired
  private TableService tableService;
  @Autowired
  private TableAvailabilityService tableAvailabilityService;

  public BookingService() {
  }

  public Booking find(Long id) {
    Optional<Booking> booking = bookingRepository.findById(id);
    if (booking.isEmpty()) {
      throw new BookingNotFoundException(id);
    }
    return booking.get();
  }

  public void updateTable(Booking booking, String tableName) {
    updateTable(booking, tableName, false);
  }


  public void updateTable(Booking booking, String tableName, Boolean isForced) {
    if (tableName.equals("")) {
      booking.removeTables();
    }
    RestaurantTable table;
    try {
      table = tableService.find(tableName);
    } catch (TableNotFoundException exception) {
      throw new BookingNotPossibleException(exception.getMessage());
    }
    if (!table.getTables().equals(booking.getTables())) {

      if (!tableAvailabilityService.areTablesFree(table.getTables(),
          booking.getStartTime(), booking.getEndTime())) {
        if (isForced) {
          freeTables(booking, table.getTables());
        } else {
          throw new BookingNotPossibleException("Table is already taken. \n " +
              "(Forcing this will remove the desired table from other bookings)", true);
        }
      }

      if (booking.getPartySize() > table.getSeats()) {
        if (!isForced) {
          throw new BookingNotPossibleException("Table is not big enough for " +
              "party", true);
        }
      }
      booking.setTables(table);
    }
    bookingRepository.save(booking);
  }

  private void freeTables(Booking booking, List<? extends RestaurantTable> tables) {
    Set<Booking> bookingsOccupyingTables =
        bookingRepository.getBookingsByTimeAndTables(
            booking.getStartTime(),
            booking.getEndTime(),
            tables);
    bookingsOccupyingTables.forEach((bookingToEmpty) -> {
      bookingToEmpty.removeTables();
      bookingRepository.save(bookingToEmpty);
    });
  }

  public List<Booking> freeTablesFromBookings(List<RestaurantTable> tablesToFree) {
    List<Booking> bookings = new ArrayList<>();
    List<Booking> bookingsThatCannotBeReassigned = new ArrayList<>();
    tablesToFree.forEach(
        (table) -> bookings.addAll(bookingRepository.getFutureBookingsByTable(table.getName())));
    bookings.forEach((booking) -> {
      Optional<RestaurantTable> availableTable =
          tableAllocatorService.getAvailableTable(booking);
      if (availableTable.isEmpty()) {
        bookingsThatCannotBeReassigned.add(booking);
      } else {
        booking.setTables(availableTable.get());
      }
    });

    return bookingsThatCannotBeReassigned;
  }


  public void updateBooking(Booking booking, Booking newBooking,
      boolean isForced) throws Exception {
    Booking oldBooking;
    oldBooking = booking.clone();
    booking.update(newBooking);

    try {
      BookingValidationService.validateBooking(booking);
    } catch (BookingRequestFormatException exception) {
      booking.update(oldBooking);
      throw exception;
    }

    if (!booking.getStartTime().isEqual(newBooking.getStartTime())) {
      try {
        setTableForBooking(booking, isForced);
      } catch (BookingNotPossibleException exception) {
        booking.update(oldBooking);
        throw exception;
      }
      booking.setDate(booking.getStartTime().toLocalDate());
      bookingRepository.save(booking);
    }
  }

  public Booking createBooking(Booking booking, User userFromRequest, boolean isForced) {
    User user = userService.createUserInDBIfNotAlreadyPresent(userFromRequest);
    checkUserForMultipleBookingsAtSameTime(user, booking.getStartTime().toLocalDate());

    booking.setUser(user);
    setTableForBooking(booking, isForced);
    booking.setDate(booking.getStartTime().toLocalDate());
    bookingRepository.save(booking);
    return booking;
  }

  private void setTableForBooking(Booking booking, Boolean isForced) {
    Optional<RestaurantTable> optionalTable =
        tableAllocatorService.getAvailableTable(booking);
    optionalTable.ifPresentOrElse(booking::setTables, () -> {
      if (!isForced) {
        throw new BookingNotPossibleException("Requested date and time are not available", true);
      }
    });
  }

  private void checkUserForMultipleBookingsAtSameTime(User user, LocalDate date) {
    List<Booking> bookings =
        bookingRepository.getBookingsByUser(user.getEmail());

    for (Booking storedBooking : bookings) {
      if (storedBooking.getStartTime().toLocalDate().equals(
          date)) {
        throw new DuplicateFoundException("You have already made a booking " +
            "on this date");
      }
    }
  }
}
