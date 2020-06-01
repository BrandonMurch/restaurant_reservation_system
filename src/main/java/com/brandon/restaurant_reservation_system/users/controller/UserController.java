package com.brandon.restaurant_reservation_system.users.controller;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.brandon.restaurant_reservation_system.users.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

	private final UserRepository userRepository;

	public UserController(
			UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/users")
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@PostMapping("/users")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		Optional<ResponseEntity<User>> userValidationException =
				UserValidationService.validateUser(user);

		if (userValidationException.isPresent()) {
			// Improperly formatted. Returns with error.
			return userValidationException.get();
		}

		user = userRepository.save(user);
		return buildUriFromUser(user);

	}


	@GetMapping("/users/{id}")
	public User getUser(@PathVariable long id) {
		// Returns user in JSON format if found, or throws an exception
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@RequestBody User newUser,
	                                    @PathVariable long id) {
		return userRepository.findById(id)
				// Returns NO_CONTENT if the user was updated successfully.
				.map(user -> {
					user.updateWith(newUser);
					Optional<ResponseEntity<User>> userValidationException =
							UserValidationService.validateUser(user);
					if (userValidationException.isPresent()) {
						return userValidationException.get();
					}
					userRepository.save(user);
					return getNoContentResponse();
				})
				// Returns CREATED if the user was not found and subsequently
				// creates the user.
				.orElseGet(() -> {
					ResponseEntity<User> response =
							this.createUser(newUser);
					if (response.getStatusCode() == HttpStatus.CREATED) {

						return ResponseEntity.created(
								ServletUriComponentsBuilder
										.fromCurrentRequest().build().toUri())
								.build();
					}
					return response;
				});
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable long id) {
		// Returns NO_CONTENT if user is successfully deleted.
		userRepository.deleteById(id);
		return getNoContentResponse();
	}

	// Allows a return in updateUser with a NO_CONTENT HttpStatus.
	public ResponseEntity<User> getNoContentResponse() {
		return ResponseEntity.noContent().build();
	}

	// Builds a URI from current request and adds on the id of the User
	public ResponseEntity<User> buildUriFromUser(User user) {
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(user.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
}