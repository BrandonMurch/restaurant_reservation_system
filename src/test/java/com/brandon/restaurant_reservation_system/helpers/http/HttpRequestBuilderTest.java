/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.http;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HttpRequestBuilderTest {

    @Mock
    private WebClient webClient;
    @InjectMocks
    private final HttpRequestBuilder httpRequest = new HttpRequestBuilder();
    private static MockWebServer server;

    @BeforeAll
    static void beforeSetUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", server.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        ReflectionTestUtils.setField(httpRequest, "webClient", webClient);
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

        server.enqueue(new MockResponse()
          .setBody(jsonExample)
          .addHeader("Content-Type", "application/json")
        );

        Optional<String> result = httpRequest.get("/json", String.class);
        if (result.isPresent()) {
            assertEquals(jsonExample, result.get());
        } else {
            fail();
        }
    }


    @Test
    void httpGetUsers() throws JsonProcessingException {
        List<User> users = Collections.singletonList(
          CreateUsersForTesting.createUser1());
        server.enqueue(new MockResponse()
          .setBody(new ObjectMapper().writeValueAsString(users))
          .addHeader("Content-Type", "application/json")
        );

        List<User> result = httpRequest.getList("/users", User.class);

        assertFalse(result.isEmpty());
        assertEquals(users, result);
    }

    @Test
    void httpGetBookings() throws JsonProcessingException {

        CreateBookingsForTest bookingStubs = new CreateBookingsForTest();

        List<Booking> bookings =
          Collections.singletonList(CreateBookingsForTest.createBookingForTwoAt19());
        server.enqueue(new MockResponse()
          .setBody(new ObjectMapper().writeValueAsString(bookings))
          .addHeader("Content-Type", "application/json")
        );
        List<Booking> result = httpRequest.getList("/bookings", Booking.class);

        assertFalse(result.isEmpty());
        assertEquals(bookings, result);
    }
}