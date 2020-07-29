package com.brandon.restaurant_reservation_system.users.service;

import com.brandon.restaurant_reservation_system.errors.ApiError;
import com.brandon.restaurant_reservation_system.errors.ValidationError;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class UserValidationService
{

	public static Optional<ResponseEntity<User>> validateUser(User user) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		Optional<ValidationError> emailError = validateEmail(user.getEmail());
		emailError.ifPresent(apiError::addSubError);
		Optional<ValidationError> phoneError =
		validatePhoneNumber(user.getPhoneNumber());
		phoneError.ifPresent(apiError::addSubError);

		switch (apiError.getSubErrors().size()) {
			case 1:
				apiError.setMessage("Validation error");
				return Optional.of(createResponseEntity(apiError));
			case 2:
				apiError.setMessage("Validation errors");
				return Optional.of(createResponseEntity(apiError));
		}
		return Optional.empty();
	}
    
    private static Optional<ValidationError> validateEmail(String email) {
		String emailRegex =
		// Created by Arushi
		// https://www.tutorialspoint.com/validate-email-address-in-java
		"^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
	if (!email.matches(emailRegex)) {
		return Optional.of(new ValidationError("User","Email", email,
				"Invalid email, should have structure: email@address.com"));
	}
	return Optional.empty();
    }
    
    private static Optional<ValidationError> validatePhoneNumber(String phone) {
	String phoneRegex = "^\\+\\d{1,3} \\d{6,14}$";
    if (!phone.matches(phoneRegex)) {
	    return Optional.of(new ValidationError("User","Phone Number", phone,
			    "Invalid phone number, " +
					    "should have structure: +{country code} {phone " +
					    "number}"));
    }
    return Optional.empty();
    }

    private static ResponseEntity<User> createResponseEntity(ApiError apiError) {
		return new ResponseEntity(apiError, apiError.getStatus());
    }
}
