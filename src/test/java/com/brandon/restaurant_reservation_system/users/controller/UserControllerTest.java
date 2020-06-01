package com.brandon.restaurant_reservation_system.users.controller;

import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.jsonToObject;
import static com.brandon.restaurant_reservation_system.helpers.json.JsonConverter.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@WebMvcTest(value = UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserRepository userRepo;

	private User mockUser;
	private List<User> users;
	@Value("${server.host}")
	private String ipAddress;
	@Value("${server.port}")
	private String port;
	private User user1, user2, updatedUser2;


	@BeforeEach
	void setUp() {
		users = initUsers();
	}

	@Test
	void getUsers() throws Exception {
		Mockito.when(userRepo.findAll()).thenReturn(this.users);

		// Submit a get request to /users.
		String uri = "/users";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri)
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		// Test a HttpStatus 200 OK is returned.
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		// Test that users are returned
		String content = result.getResponse().getContentAsString();
		User[] users = jsonToObject(content, User[].class);
		assertTrue(users.length > 0);
		assertEquals("John", users[0].getFirstName());
	}

	@Test
	void createUser() throws Exception {
		mockUser.setId(3);
		Mockito.when(userRepo.save(mockUser)).thenReturn(mockUser);

		// Send a Post request to /users with a new user in the body
		String uri = "/users";
		String userJson = objectToJson(mockUser);
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(uri)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		// Test that a HttpStatus 201 CREATED is returned.
		int status = result.getResponse().getStatus();
		assertEquals(201, status);

		// Test that the uri to the created user is returned.
		assertEquals("http://" + ipAddress + "/users/3",
				result.getResponse().getHeader(HttpHeaders.LOCATION));
	}

	@Test
	void getUser() throws Exception {
		Mockito.when(userRepo.findById((long)2)).thenReturn(
				java.util.Optional.ofNullable(users.get(1)));

		// Get call to /users/2.
		String uri = "/users/2";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri)
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		// Check status is 200 OK
		int status = result.getResponse().getStatus();
		assertEquals(200, status);

		// Check a user was returned;
		User user = jsonToObject(result.getResponse().getContentAsString(),
				User.class);
		assertEquals(user, user2);
	}

	@Test
	void updateUser() throws Exception {

		Mockito.when(userRepo.findById((long)2)).thenReturn(
				java.util.Optional.ofNullable(users.get(1)));
		Mockito.when(userRepo.save(updatedUser2)).thenReturn(updatedUser2);

		// Put call to /users/2
		String uri = "/users/2";
		String userJson = objectToJson(updatedUser2);
		MvcResult result = mvc.perform(MockMvcRequestBuilders.put(uri)
				.accept(MediaType.APPLICATION_JSON)
				.content(userJson).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		// Check status is 204 NO_CONTENT
		int status = result.getResponse().getStatus();
		assertEquals(204, status);
	}

	@Test
	void deleteUser() throws Exception {

		// Delete to /useres/2
		String uri = "/users/2";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(uri))
				.andReturn();

		// Check status is 204 NO_CONTENT
		int status = result.getResponse().getStatus();
		assertEquals(204, status);
	}

	public List<User> initUsers() {

		// Set up an array with 2 users
		// Set up 1 mock user and 1 updated user
		CreateUsersForTesting createUser = new CreateUsersForTesting();
		user1 = createUser.createUser1();
		user2 = createUser.createUser2();
		updatedUser2 = createUser.createUpdatedUser2();
		mockUser = createUser.createUser3();
		return Arrays.asList(user1, user2);
	}
}