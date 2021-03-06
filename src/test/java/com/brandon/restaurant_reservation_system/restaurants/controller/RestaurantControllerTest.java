/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.brandon.restaurant_reservation_system.TestWebSecurityConfig;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingDates;
import com.brandon.restaurant_reservation_system.restaurants.services.BookingTimes;
import com.brandon.restaurant_reservation_system.tables.service.TableService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = RestaurantController.class)
@ActiveProfiles("Test")
@Import(TestWebSecurityConfig.class)
class RestaurantControllerTest {

  @MockBean
  private TableService tableService;

  @MockBean
  private BookingTimes bookingTimes;

  @MockBean
  private BookingDates bookingDates;

  @Autowired
  private MockMvc mvc;

    @Test
    void getAvailableBookingDates() throws Exception {
      LocalDate today = LocalDate.now();
      String todayString = "[\"" + today.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\"]";

      SortedSet<LocalDate> dates = new TreeSet<>();
      dates.add(today);

      Mockito
          .when(bookingDates.getAll())
          .thenReturn(dates);

      MvcResult result =
          mvc.perform(MockMvcRequestBuilders
              .get("/restaurant/availability")
              .contentType(MediaType.APPLICATION_JSON))
              .andReturn();
        int status = result.getResponse().getStatus();
        assertEquals(200, status);

        String content = result.getResponse().getContentAsString();
      assertEquals(todayString, content);
    }

    @Test
    void getAvailableBookingTimes() throws Exception {
        LocalTime now = LocalTime.now();
        LocalTime oneHour = now.plusHours(1);

        SortedSet<LocalTime> set = new TreeSet<>();
        set.add(now);
        set.add(oneHour);

        String nowString = now.format(DateTimeFormatter.ISO_LOCAL_TIME);
        String oneHourString = oneHour.format(DateTimeFormatter.ISO_LOCAL_TIME);

        String setJson = "[\"" + nowString + "\",\"" + oneHourString + "\"]";

      when(bookingTimes.getAvailableBySize(2, LocalDate.now()))
          .thenReturn(set);

        String dateString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        String uri = "/restaurant/availability?date=" + dateString + "&size=2";

        MvcResult result =
          mvc.perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(200, status);

        String content = result.getResponse().getContentAsString();
        assertEquals(setJson, content);

        uri = "/restaurant/availability?date=\"" + dateString + "\"&size=2";

        result =
          mvc.perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        status = result.getResponse().getStatus();
        assertEquals(400, status);
    }
}