package com.brandon.restaurant_reservation_system.helpers.http;

import com.brandon.restaurant_reservation_system.bookings.exceptions.BookingNotFoundException;
import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class HttpRequestBuilder {

	@Value("${server.port:8080}")
	private  String serverPort;
	@Value("${server.host:localhost}")
	private  String ipAddress;
	private final RestTemplate restTemplate =
			new RestTemplateBuilder().build();

	public HttpRequestBuilder() {}

	public Optional<String> httpGetJson(String relativePath) {
		return Optional.ofNullable(restTemplate
				.getForEntity(constructUrl(relativePath), String.class)
				.getBody());
	}

	public List<User> httpGetUsers(String relativePath)
			throws UserNotFoundException {
		ResponseEntity<User[]> response = restTemplate
				.getForEntity(constructUrl(relativePath), User[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new UserNotFoundException("Users not found");
		}
		User[] userArray = response.getBody();
		if (userArray == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList(Arrays.asList(userArray));
		}
	}

	public List<Booking> httpGetBookings(String relativePath)
			throws BookingNotFoundException {
		ResponseEntity<Booking[]> response = restTemplate
				.getForEntity(constructUrl(relativePath), Booking[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new BookingNotFoundException("Bookings not found");
		}
		Booking[] bookingArray = response.getBody();
		if (bookingArray == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList(Arrays.asList(bookingArray));
		}
	}

	private String constructUrl(String relativePath) {
		return "http://" + ipAddress + ":" + serverPort + relativePath;
	}
}
