package com.brandon.restaurant_reservation_system.users.service;

import static org.junit.jupiter.api.Assertions.*;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.hibernate.hql.internal.ast.tree.ResolvableNode;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Validation;

class UserValidationServiceTest {

	User user;

	@BeforeEach
	void Setup() {
		user = new User();
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
	    ResponseEntity<User> response = new ResponseEntity(apiError,
			    apiError.getStatus());

    	user.setEmail("this is not an email.com");
    	user.setPhoneNumber("+61 14483849599");

	    // Improperly formatted email.
	    assertTrue(UserValidationService.validateUser(user).isPresent());
	    assertEquals(UserValidationService
			    .validateUser(user).get().getStatusCode(), response.getStatusCode());

	    // Properly formatted email.
	    user.setEmail("email@email.com");
	    assertFalse(UserValidationService.validateUser(user).isPresent());
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
	    ResponseEntity<User> response = new ResponseEntity(apiError,
			    apiError.getStatus());


	    user.setEmail("email@email.com");
	    user.setPhoneNumber("2334349");

	    // Improperly formatted phone number.
	    assertTrue(UserValidationService.validateUser(user).isPresent());
	    assertEquals(UserValidationService
			    .validateUser(user).get().getStatusCode(), response.getStatusCode());

	    // Properly formatted email.
	    user.setPhoneNumber("+61 48485800484");
	    assertFalse(UserValidationService.validateUser(user).isPresent());
    }
}
