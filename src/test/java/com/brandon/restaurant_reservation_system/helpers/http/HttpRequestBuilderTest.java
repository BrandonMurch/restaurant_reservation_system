/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.http;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class HttpRequestBuilderTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private final HttpRequestBuilder httpRequest = new HttpRequestBuilder();


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(httpRequest, "serverPort", "8080");
        ReflectionTestUtils.setField(httpRequest, "ipAddress", "localhost");
    }

    @Test
    void httpGetJson() {
        String jsonExample = "{\n" +
          "    \"glossary\": {\n" +
          "        \"title\": \"example glossary\",\n" +
          "\t\t\"GlossDiv\": {\n" +
          "            \"title\": \"S\",\n" +
          "\t\t\t\"GlossList\": {\n" +
          "                \"GlossEntry\": {\n" +
          "                    \"ID\": \"SGML\",\n" +
          "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
          "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
          "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
          "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
          "\t\t\t\t\t\"GlossDef\": {\n" +
          "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
          "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
          "                    },\n" +
          "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
          "                }\n" +
          "            }\n" +
          "        }\n" +
          "    }\n" +
          "}";

        Mockito
          .when(restTemplate.getForEntity(any(String.class), eq(String.class)))
          .thenReturn(new ResponseEntity<>(jsonExample, HttpStatus.OK));

        Optional<String> result = httpRequest.httpGetJson("/json");
        if (result.isPresent()) {
            assertEquals(jsonExample, result.get());
        } else {
            fail();
        }
    }

    @Test
    void httpGetUsers() {
        User[] users = {CreateUsersForTesting.createUser1()};
        Mockito
          .when(restTemplate.getForEntity(any(String.class), eq(User[].class)))
          .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        List<User> result = httpRequest.httpGetUsers("/users");

        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(users), result);
    }

    @Test
    void httpGetBookings() {

        CreateBookingsForTest bookingStubs = new CreateBookingsForTest();

        Booking[] bookings = {bookingStubs.createBookingForTwoAt19()};
        Mockito
          .when(restTemplate.getForEntity(any(String.class), eq(Booking[].class)))
          .thenReturn(new ResponseEntity<>(bookings, HttpStatus.OK));

        List<Booking> result = httpRequest.httpGetBookings("/bookings");

        assertFalse(result.isEmpty());
        assertEquals(Arrays.asList(bookings), result);
    }
}