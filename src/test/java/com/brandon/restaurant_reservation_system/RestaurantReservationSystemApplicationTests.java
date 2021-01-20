/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.brandon.restaurant_reservation_system.bookings.CreateBookingsForTest;
import com.brandon.restaurant_reservation_system.bookings.controller.BookingController;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.bookings.model.RequestBodyUserBooking;
import com.brandon.restaurant_reservation_system.restaurants.controller.RestaurantController;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.controller.UserController;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestWebSecurityConfig.class)
@ActiveProfiles("Test")
class RestaurantReservationSystemApplicationTests {

  // TODO: update these to test for authentication as well
  @MockBean
  UserPasswordEncoder passwordEncoder;
  @MockBean
  AuthenticationManager authenticationManager;
  @Autowired
  private BookingController bookingController;
  @Autowired
  private RestaurantController restaurantController;
  @Autowired
  private UserController userController;
  @Autowired
  private WebTestClient testClient;

  @Test
  void getBookings() {
    List<Booking> bookings = testClient
        .get().uri("/bookings")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Booking.class).returnResult().getResponseBody();

    if (bookings != null) {
      assertTrue(bookings.size() > 0);
    } else {
      fail();
    }
  }

  @Test
  void createBooking() {
    List<Booking> bookings = testClient
        .get().uri("/bookings")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Booking.class)
        .returnResult().getResponseBody();

    int size = 0;
    if (bookings != null) {
      size = bookings.size();
    }
    Booking booking = CreateBookingsForTest.createBookingForTwoAt19();
    User user = CreateUsersForTesting.createUser1();
    RequestBodyUserBooking body = new RequestBodyUserBooking(user, booking);

    testClient
        .post().uri("/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().exists("Location")
        .expectBody().isEmpty();

    testClient
        .get().uri("/bookings")
        .exchange()
        .expectBodyList(Booking.class).hasSize(size + 1);

  }

  @Test
  void deleteBooking() {
    List<Booking> bookings = testClient
        .get().uri("/bookings")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Booking.class).returnResult().getResponseBody();

    int size = 0;
    if (bookings != null) {
      size = bookings.size();
    }

    testClient
        .delete().uri("/bookings/10000")
        .exchange()
        .expectStatus().is2xxSuccessful();

    testClient
        .get().uri("/bookings")
        .exchange()
        .expectBodyList(Booking.class).hasSize(size - 1);
  }

  @Test
  void getUsers() {
    List<User> users = testClient
        .get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(User.class).returnResult().getResponseBody();

    if (users != null) {
      assertTrue(users.size() > 0);
    }
  }

  @Test
  void createUser() {
    List<User> users = testClient
        .get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(User.class).returnResult().getResponseBody();

    int size = 0;
    if (users != null) {
      size = users.size();
    }
    User user = CreateUsersForTesting.createUser2();

    testClient
        .post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange()
        .expectStatus().isCreated()
        .expectBody().isEmpty();
    testClient
        .get().uri("/users")
        .exchange()
        .expectBodyList(User.class).hasSize(size + 1);

  }

  @Test
  void deleteUser() {
    List<User> users = testClient
        .get().uri("/users")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(User.class).returnResult().getResponseBody();

    int size = 0;
    if (users != null) {
      size = users.size();
    }

    testClient
        .delete().uri("/users/10001")
        .exchange()
        .expectStatus().is2xxSuccessful();

    testClient
        .get().uri("/users")
        .exchange()
        .expectBodyList(Booking.class).hasSize(size - 1);
  }

  @Test
  void getTables() {
    List<String> tables = testClient
        .get().uri("/tables")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(String.class).returnResult().getResponseBody();

    if (tables != null) {
      assertTrue(tables.size() > 0);
    } else {
      fail("List has not been properly initialized");
    }
  }

  @Test
  void createTable() {
    RestaurantTable table = new RestaurantTable("newTableName", 100, 1000000);

    testClient
        .post().uri("/tables")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(table)
        .exchange()
        .expectStatus().isCreated()
        .expectBody().isEmpty();
    testClient
        .get().uri("/tables")
        .exchange()
        .expectBodyList(RestaurantTable.class).contains(table);

  }

  @Test
  void deleteTable() {
    RestaurantTable table = new RestaurantTable("newTableName", 100, 1000000);

    testClient
        .post().uri("/tables")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(table)
        .exchange();

    testClient
        .get().uri("/tables")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(RestaurantTable.class).contains(table);

    testClient
        .delete().uri("/tables/" + table.getName())
        .exchange()
        .expectStatus().is2xxSuccessful();

    testClient
        .get().uri("/tables")
        .exchange()
        .expectBodyList(RestaurantTable.class).doesNotContain(table);
  }


}
