package com.brandon.restaurant_reservation_system.users.service;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationServiceTest {

	private final CreateUsersForTesting testUser = new CreateUsersForTesting();
	private User user;

	@BeforeEach
	void Setup() {
		user = testUser.createUser1();
	}


	@Test
	void testValidateEmail() {
		// set up an ApiError mimicking a validation error
		ValidationError error = new ValidationError("User", "Email",
				"this is not an email.com",
				"Invalid email, should have structure: email@address.com");
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		apiError.addSubError(error);
		apiError.setMessage("Validation error");
		ResponseEntity<ApiError> response = new ResponseEntity<>(apiError,
				apiError.getStatus());

		User invalidEmailUser = testUser.invalidEmail();

		// Improperly formatted email.
		assertTrue(UserValidationService.validateUser(
				invalidEmailUser).isPresent());
		assertEquals(UserValidationService
						.validateUser(invalidEmailUser).get().getStatusCode(),
				response.getStatusCode());

		// Properly formatted email.
		assertFalse(UserValidationService.validateUser(this.user).isPresent());
	}

    @Test
    void testValidatePhoneNumber() {
		// set up an ApiError mimicking a validation error
		ValidationError error = new ValidationError("User",
				"Phone Number", "+ 123456789 012",
				"Invalid phone number, " +
						"should have structure: +{country code} {phone number}");

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		apiError.addSubError(error);
		apiError.setMessage("Validation error");
		ResponseEntity<ApiError> response = new ResponseEntity<>(apiError,
				apiError.getStatus());

		User invalidPhoneUser = testUser.invalidPhone();

		// Improperly formatted phone number.
		assertTrue(UserValidationService.validateUser(
				invalidPhoneUser).isPresent());
		assertEquals(UserValidationService
						.validateUser(invalidPhoneUser).get().getStatusCode(),
				response.getStatusCode());

		// Properly formatted email.
		assertFalse(UserValidationService.validateUser(user).isPresent());
	}
}
