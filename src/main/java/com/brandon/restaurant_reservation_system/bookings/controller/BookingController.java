/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingRequestFormatException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingService;
import com.brandon.restaurant_reservation_system.bookings.services.BookingValidationService;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler;
import com.brandon.restaurant_reservation_system.restaurants.model.Restaurant;
import com.brandon.restaurant_reservation_system.restaurants.services.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.restaurants.services.TableService;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/bookings")
public class BookingController {

  private final DateTimeFormatter dateFormat = GlobalVariables.getDateFormat();
  private final DateTimeFormatter dateTimeFormat =
      GlobalVariables.getDateTimeFormat();
  @Autowired
  private Restaurant restaurant;
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private BookingService bookingService;
  @Autowired
  private TableAvailabilityService tableAvailability;
  @Autowired
  private TableService tableHandler;

  public BookingController() {
  }

  private List<Booking> parseDateTimesAndFindBookings(String start,
      String end) {
    LocalDateTime parsedStartTime =
        DateTimeHandler.parseDateTime(start,
            dateTimeFormat);
    LocalDateTime parsedEndTime = DateTimeHandler.parseDateTime(end,
        dateTimeFormat);
    return bookingRepository.getBookingsDuringTime(parsedStartTime,
        parsedEndTime);
  }

  private List<Booking> parseDateAndFindBookings(String date) {
    LocalDateTime parsedDateTime = DateTimeHandler.parseDate(date,
        dateFormat).atStartOfDay();
    LocalDateTime nextDay = parsedDateTime.plusDays(1);

    return bookingRepository.getBookingsBetweenDates(parsedDateTime,
        nextDay);
  }

  private List<Booking> parseStartTimeAndFindBookings(String start) {
    LocalDateTime parsedStartTime = DateTimeHandler.parseDateTime(
        start, dateTimeFormat);
    return bookingRepository.getBookingsByStartTime(parsedStartTime);
  }

  @GetMapping(value = "")
  public List<Booking> getBookings(
      @RequestParam(required = false) String startTime,
      @RequestParam(required = false) String endTime,
      @RequestParam(required = false) String date) {
    if (startTime != null && endTime != null) {
      return parseDateTimesAndFindBookings(startTime, endTime);
    } else if (startTime != null) {
      return parseStartTimeAndFindBookings(startTime);
    } else if (date != null) {
      return parseDateAndFindBookings(date);
    } else {
      return bookingRepository.findAll();
    }
  }

  @GetMapping("/dailyCount")
  public ResponseEntity<?> getBookingsPerDay() {
    Map<LocalDate, Integer> map = restaurant.getBookingsPerDate();
    return ResponseEntity.ok(restaurant.getBookingsPerDate());
  }

  @GetMapping("/{bookingId}")
  public Booking getBookingById(@PathVariable long bookingId) {
    return bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingNotFoundException(bookingId));
  }

  @PutMapping("{bookingId}/setTable")
  public void updateBookingWithTable(@PathVariable long bookingId,
      @RequestBody String tableName,
      HttpServletRequest request,
      HttpServletResponse response) {

    Booking booking = bookingService.find(bookingId);
    bookingService.updateTable(booking, tableName, isRequestForced(request));

    try {
      sendResponse(response, HttpStatus.NO_CONTENT.value(), "Booking " +
          "table successfully updated.");
    } catch (IOException e) {
      e.printStackTrace();
      throw new InternalError("Response sending failed");
    }
  }

  @PutMapping("/{bookingId}")
  public void updateBooking(@RequestBody Booking newBooking,
      @PathVariable long bookingId,
      HttpServletRequest request,
      HttpServletResponse response
  ) throws Exception {
    Optional<Booking> result =
        bookingRepository.findById(bookingId);

    if (result.isPresent()) {
      Booking booking = result.get();
      bookingService.updateBooking(booking,
          newBooking, isRequestForced(request));
      try {
        sendResponse(response, HttpStatus.NO_CONTENT.value(), "Booking successfully " +
            "updated.");
      } catch (IOException e) {
        e.printStackTrace();
        throw new InternalError("Response sending failed");
      }

      Boolean hasDateChanged = !newBooking.getDate().isEqual(booking.getDate());
      Boolean hasSizeChanged = !newBooking.getPartySize().equals(booking.getPartySize());
      if (hasDateChanged || hasSizeChanged) {
        restaurant.removeBookingFromDate(booking.getDate(), booking.getPartySize());
        restaurant.addBookingToDate(newBooking.getDate(), newBooking.getPartySize());
      }
    } else {
      createBooking(
          new RequestBodyUserBooking(newBooking.getUser(), newBooking),
          request, response);
    }
  }

  @PostMapping("")
  public void createBooking(
      @RequestBody RequestBodyUserBooking body,
      HttpServletRequest request,
      HttpServletResponse response) {

    Booking booking = body.getBooking();
    BookingValidationService.validateBooking(booking);
    User user = new User(body.getUser());
    if (user.getUsername() == null || user.getUsername().isEmpty()) {
      throw new BookingRequestFormatException("Email is a required field");
    }

    Booking result = bookingService.createBooking(booking, user, isRequestForced(request));
    try {
      sendResponse(response, buildUriFromBooking(result));
    } catch (IOException e) {
      e.printStackTrace();
    }
    restaurant.removeDateIfUnavailable(result.getStartTime().toLocalDate());
    restaurant.addBookingToDate(result.getDate(), result.getPartySize());
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<String> deleteBooking(@PathVariable long bookingId) {
    Optional<Booking> booking = bookingRepository.findById(bookingId);
    booking.ifPresent(
        booking1 -> restaurant.removeBookingFromDate(booking1.getDate(), booking1.getPartySize()));
    bookingRepository.deleteById(bookingId);
    return ResponseEntity.noContent().build();
  }

  private boolean isRequestForced(HttpServletRequest request) {
    String forceHeader = request.getHeader("Force");
    return (forceHeader != null && !forceHeader.isEmpty());
  }

  private ResponseEntity<String> buildUriFromBooking(Booking booking) {
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .replacePath("/bookings")
        .path("/{id}")
        .buildAndExpand(booking.getId())
        .toUri();
    return ResponseEntity.created(location).build();
  }

  private void sendResponse(HttpServletResponse response, ResponseEntity<?> entity)
      throws IOException {
    PrintWriter writer = response.getWriter();
    response.setStatus(entity.getStatusCodeValue());
    entity.getHeaders().forEach((key, value) -> response.setHeader(key,
        value.toString()));
    if (entity.getBody() != null) {
      writer.print(entity.getBody());
    }
    writer.flush();
    writer.close();
  }

  private void sendResponse(HttpServletResponse response, int status,
      Object body) throws IOException {
    PrintWriter writer = response.getWriter();
    response.setStatus(status);
    writer.print(body);
    writer.flush();
    writer.close();
  }

}
