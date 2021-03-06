/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.controller;

import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.jsonToObject;
import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.brandon.restaurant_reservation_system.GlobalVariables;
import com.brandon.restaurant_reservation_system.TestWebSecurityConfig;
import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.data.BookingRepository;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.services.BookingService;
import com.brandon.restaurant_reservation_system.restaurants.CreateTableForTest;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.tables.service.TableAvailabilityService;
import com.brandon.restaurant_reservation_system.tables.service.TableService;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SuppressWarnings("unused")
@ActiveProfiles("Test")
@WebMvcTest(BookingController.class)
@Import(TestWebSecurityConfig.class)
class BookingControllerTest {

  @SuppressWarnings("unused")
  @Captor
  private ArgumentCaptor<List<RestaurantTable>> listArgumentCaptor;
  @SuppressWarnings("unused")
  @MockBean
  private BookingRepository bookingRepository;
  @SuppressWarnings("unused")
  @MockBean
  private BookingService bookingService;
  @SuppressWarnings("unused")
  @MockBean
  private TableAvailabilityService tableAvailability;
  @SuppressWarnings("unused")
  @MockBean
  private TableService tableHandler;
  @Autowired
  private MockMvc mvc;

  private List<Booking> bookings;
  private Booking booking1;
  private Booking updatedBooking2;

  @SuppressWarnings("unused")
  @BeforeEach
  void setUp() {
    booking1 = CreateBookingsForTest.createBookingForTwoAt19();
    User user = booking1.getUser();
    Booking booking2 = CreateBookingsForTest.createBookingForFourAt20();
    updatedBooking2 = CreateBookingsForTest.createUpdatedBookingForFour();
    this.bookings = Arrays.asList(booking1, booking2);
  }

  @Test
  void getBookings() throws Exception {
    Mockito.when(bookingRepository.findAll()).thenReturn(this.bookings);

    String uri = "/bookings";
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    Booking[] bookings = jsonToObject(content, Booking[].class);
    assertTrue(bookings.length > 0);
    assertEquals(2, bookings[0].getPartySize());
  }

  @Test
  void getBookingsByTime() throws Exception {

    String start = "2020-10-11T18:00:00.00";
    String end = "2020-10-11T21:00:00.00";
    LocalDateTime startTime = LocalDateTime.parse(start);
    LocalDateTime endTime = LocalDateTime.parse(end);
    Mockito.when(bookingRepository
        .getBookingsDuringTime(startTime, endTime))
        .thenReturn(this.bookings);

    String uri = "/bookings?startTime=" + start + "&endTime=" + end;
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
            MediaType.APPLICATION_JSON)).andReturn();
    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    Booking[] bookings = jsonToObject(content, Booking[].class);
    assertEquals(2, bookings.length);
  }

  @Test
  void getBookingsByStartTime() throws Exception {
    LocalDateTime startTime = booking1.getStartTime();
    Mockito.when(bookingRepository
        .getBookingsByStartTime(startTime))
        .thenReturn(this.bookings.stream()
            .filter(booking -> booking.getStartTime().equals(
                startTime))
            .collect(Collectors.toList()));

    String uri = "/bookings?startTime=" + startTime.format(GlobalVariables.getDateTimeFormat());
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
            MediaType.APPLICATION_JSON)).andReturn();
    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    Booking[] bookings = jsonToObject(content, Booking[].class);
    assertEquals(1, bookings.length);
  }

  @Test
  void getBookingsByDate() throws Exception {
    String date = "2020-12-11";
    LocalDateTime startDate = LocalDate.parse(date).atStartOfDay();
    LocalDateTime endDate = startDate.plusDays(1);
    Mockito.when(bookingRepository
        .getBookingsBetweenDates(startDate, endDate))
        .thenReturn(this.bookings);

    String uri = "/bookings?date=" + date;
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
            MediaType.APPLICATION_JSON)).andReturn();
    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    Booking[] bookings = jsonToObject(content, Booking[].class);
    assertEquals(2, bookings.length);

  }


  @Test
  void getBookingById() throws Exception {
    Mockito.when(bookingService.find((long) 1)).thenReturn(this.booking1);

    String uri = "/bookings/1";
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.get(uri).contentType(
            MediaType.APPLICATION_JSON)).andReturn();
    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    Booking booking = jsonToObject(content, Booking.class);
    assertEquals(booking.getPartySize(), booking1.getPartySize());

  }

  @Test
  void updateBookingNotPresent() throws Exception {
    Mockito.when(bookingRepository.findById((long) 1))
        .thenReturn(Optional.empty());
    Mockito.when(bookingService.createBooking(any(Booking.class), any(User.class),
        any(Boolean.class)))
        .thenReturn(updatedBooking2);

    String uri = "/bookings/" + updatedBooking2.getId();
    String bookingJson = objectToJson(updatedBooking2);
    MockHttpServletResponse response =
        mvc.perform(MockMvcRequestBuilders.put(uri)
            .accept(MediaType.APPLICATION_JSON)
            .content(bookingJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    assertEquals(201, response.getStatus());
    assertTrue(response.getContentAsString().isEmpty());
    assertNotNull(response.getHeader("Location"));
  }

  @Test
  void updateBookingAlreadyPresent() throws Exception {
    Mockito.when(bookingRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(this.updatedBooking2));

    String uri = "/bookings/" + updatedBooking2.getId();
    String bookingJson = objectToJson(updatedBooking2);
    Booking bookingFromJson = jsonToObject(bookingJson, Booking.class);
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.put(uri)
            .accept(MediaType.APPLICATION_JSON)
            .content(bookingJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertEquals(204, result.getResponse().getStatus());
  }

  @Test
  void updateBookingWithTable() throws Exception {
    Mockito
        .when(bookingRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(this.updatedBooking2));
    Mockito
        .when(tableHandler.find(any(String.class)))
        .thenReturn(CreateTableForTest.getTable1());
    Mockito
        .when(tableAvailability.areTablesFree(Mockito.notNull(),
            any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(true);

    String uri = "/bookings/" + updatedBooking2.getId() + "/setTable";
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.put(uri)
            .accept(MediaType.APPLICATION_JSON)
            .content("21, 22")
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertEquals(204, result.getResponse().getStatus());
  }

  @SuppressWarnings("unused")
  @Test
  void updateBookingWithCombination() throws Exception {
    List<RestaurantTable> tables = Arrays.asList(
        CreateTableForTest.getTable1(), CreateTableForTest.getTable2(),
        CreateTableForTest.getTable3());

    Mockito.when(bookingRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(this.updatedBooking2));
    Mockito
        .when(tableHandler.find(any(String.class)))
        .thenReturn(CreateTableForTest.getCombination1());
    Mockito.when(tableAvailability.areTablesFree(Mockito.notNull(),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

    String uri = "/bookings/" + updatedBooking2.getId() + "/setTable";
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.put(uri)
            .accept(MediaType.APPLICATION_JSON)
            .content("21, 22, 23")
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertEquals(204, result.getResponse().getStatus());
  }

}