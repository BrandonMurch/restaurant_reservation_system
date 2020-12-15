/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.controller;

import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.jsonToObject;
import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brandon.restaurant_reservation_system.TestWebSecurityConfig;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(value = UserController.class)
@ActiveProfiles("Test")
@Import(TestWebSecurityConfig.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepo;

  private User mockUser;
  private List<User> users;
  @Value("${server.host}")
  private String ipAddress;
  private User user1, user2, updatedUser2;


  @BeforeEach
  void setUp() {
    users = initUsers();
  }

  @Test
  void getUsers() throws Exception {
    Mockito.when(userRepo.findAll()).thenReturn(this.users);

    String uri = "/users";
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri)
        .contentType(MediaType.APPLICATION_JSON)).andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    User[] users = jsonToObject(content, User[].class);
    assertTrue(users.length > 0);
    assertEquals(this.users.get(0).getFirstName(), users[0].getFirstName());
  }

  @Test
  void getUsersByEmail() throws Exception {
    Mockito
        .when(userRepo.findByEmail(this.user1.getEmail()))
        .thenReturn(java.util.Optional.ofNullable(this.user1));

    String uri = "/users?email=" + this.users.get(0).getEmail();
    MvcResult result = mvc
        .perform(MockMvcRequestBuilders.get(uri)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    String content = result.getResponse().getContentAsString();
    User[] users = jsonToObject(content, User[].class);
    assertTrue(users.length > 0);
    assertEquals(this.user1.getFirstName(), users[0].getFirstName());
  }

  @Test
  void createUser() throws Exception {
    mockUser.setId(3);
    Mockito.when(userRepo.save(mockUser)).thenReturn(mockUser);

    String uri = "/users";
    String userJson = objectToJson(mockUser);
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri)
        .accept(MediaType.APPLICATION_JSON)
        .content(userJson).contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(201, status);

    assertEquals("http://" + ipAddress + "/users/3",
        result.getResponse().getHeader(HttpHeaders.LOCATION));
  }

  @Test
  void getUser() throws Exception {
    Mockito.when(userRepo.findById((long) 2)).thenReturn(
        java.util.Optional.ofNullable(users.get(1)));

    String uri = "/users/2";
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri)
        .contentType(MediaType.APPLICATION_JSON)).andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(200, status);

    User user = jsonToObject(result.getResponse().getContentAsString(),
        User.class);
    assertEquals(user, user2);
  }

  @Test
  void updateUser() throws Exception {

    Mockito.when(userRepo.findById((long) 2)).thenReturn(
        java.util.Optional.ofNullable(users.get(1)));
    Mockito.when(userRepo.save(updatedUser2)).thenReturn(updatedUser2);

    String uri = "/users/2";
    String userJson = objectToJson(updatedUser2);
    MvcResult result = mvc.perform(MockMvcRequestBuilders.put(uri)
        .accept(MediaType.APPLICATION_JSON)
        .content(userJson).contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(204, status);
  }

  @Test
  void deleteUser() throws Exception {

    String uri = "/users/2";
    MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(uri))
        .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(204, status);
  }

  public List<User> initUsers() {
    CreateUsersForTesting createUser = new CreateUsersForTesting();
    user1 = CreateUsersForTesting.createUser1();
    user2 = CreateUsersForTesting.createUser2();
    updatedUser2 = CreateUsersForTesting.createUpdatedUser2();
    mockUser = createUser.createUser3();
    return Arrays.asList(user1, user2);
  }
}