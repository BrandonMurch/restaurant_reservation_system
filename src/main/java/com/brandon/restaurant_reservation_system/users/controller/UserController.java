/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.controller;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.brandon.restaurant_reservation_system.users.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	public UserController() {
	}

	@GetMapping("")
	public List<User> getUsers(@RequestParam(required = false) String email) {
		if (email != null) {
			Optional<User> user = userRepository.findByEmail(email);
			return user.map(Collections::singletonList).orElse(
					Collections.emptyList());
		}
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User newUser,
	                                    @PathVariable long id) {
		return userRepository.findById(id)
				.map(user -> {
					user.updateWith(newUser);
					Optional<ResponseEntity<ApiError>> userValidationException =
					UserValidationService.validateUser(user);
					if (userValidationException.isPresent()) {
						return userValidationException.get();
					}
					userRepository.save(user);
					return getNoContentResponse();
				})
				.orElseGet(() -> {
					ResponseEntity<?> response =
							this.createUser(newUser);
					if (response.getStatusCode() == HttpStatus.CREATED) {
						// uri must be adjusted, or else the new id is appended to the
						// current url. Example: /users/2/2, when we only want /users/2
						return ResponseEntity.created(
						ServletUriComponentsBuilder
						.fromCurrentRequest().build().toUri())
						.build();
					}
					return response;
				});
	}

	@PostMapping("")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		Optional<ResponseEntity<ApiError>> userValidationException =
		UserValidationService.validateUser(user);

		if (userValidationException.isPresent()) {
			// Improperly formatted. Returns with error.
			return userValidationException.get();
		}

		user = new User(user);
		user = userRepository.save(user);
		return buildUriFromUser(user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable long id) {
		// Returns NO_CONTENT if user is successfully deleted.
		userRepository.deleteById(id);
		return getNoContentResponse();
	}

	// Builds a URI from current request and adds on the id of the User
	private ResponseEntity<String> buildUriFromUser(User user) {
		URI location = ServletUriComponentsBuilder
		.fromCurrentRequest()
		.path("/{id}")
		.buildAndExpand(user.getId())
		.toUri();
		return ResponseEntity.created(location).build();
	}

	private ResponseEntity<String> getNoContentResponse() {
		return ResponseEntity.noContent().build();
	}
}