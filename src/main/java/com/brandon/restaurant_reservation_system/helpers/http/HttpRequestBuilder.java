/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HttpRequestBuilder {

	@Value("${server.port}")
	private String port;
	@Value("${server.host:localhost}")
	private String ipAddress;
	private WebClient webClient;

	public HttpRequestBuilder() {
	}

	@PostConstruct
	private void postConstruct() {
		String url = "http://" + ipAddress + ":" + port;
		webClient = WebClient.builder().baseUrl(url).build();
	}

	public <T> Optional<T> get(String relativePath, Class<T> outputClass) {
		ResponseEntity<T> response = webClient
		.get().uri(relativePath)
		.retrieve()
		.toEntity(outputClass)
		.block();

		if (response == null) {
			throw new RuntimeException("Unexpected Error trying to connect to " + relativePath);
		} else if (!response.getStatusCode().equals(HttpStatus.OK)) {
			return Optional.empty();
		}
		return Optional.ofNullable(response.getBody());
	}

	public <T> List<T> getList(String relativePath, Class<T> outputClass) {
		ResponseEntity<List<T>> response = webClient
		.get().uri(relativePath)
		.retrieve()
		.toEntityList(outputClass)
		.block();

		if (response == null) {
			throw new RuntimeException("Unexpected Error trying to connect to " + relativePath);
		} else if (!response.getStatusCode().equals(HttpStatus.OK)) {
			return Collections.emptyList();
		}
		return response.getBody();
	}

	//	public Optional<String> httpGetJson(String relativePath) {
	//
	//		return webClient
	//		.get().uri(relativePath)
	//		.retrieve()
	//		.bodyToMono(String.class)
	//		.blockOptional();
	//	}
	//
	//	public List<User> httpGetUsers(String relativePath)
	//	throws UserNotFoundException {
	//		ResponseEntity<List<User>> response = webClient
	//		.get().uri(relativePath)
	//		.retrieve()
	//		.toEntityList(User.class)
	//		.block();
	//
	//
	//		if (response == null) {
	//			throw new RuntimeException("Unexpected Error trying to connect to " + relativePath);
	//		} else if (!response.getStatusCode().equals(HttpStatus.OK)) {
	//			throw new UserNotFoundException("Users not found");
	//		}
	//		return response.getBody();
	//	}
	//
	//	public List<Booking> httpGetBookings(String relativePath)
	//	throws BookingNotFoundException {
	//		ResponseEntity<List<Booking>> response = webClient
	//		.get().uri(relativePath)
	//		.retrieve()
	//		.toEntityList(Booking.class)
	//		.block();
	//
	//
	//		if (response == null) {
	//			throw new RuntimeException("Unexpected Error trying to connect to " + relativePath);
	//		} else if (!response.getStatusCode().equals(HttpStatus.OK)) {
	//			throw new UserNotFoundException("Bookings not found");
	//		}
	//		return response.getBody();
	//	}
}
