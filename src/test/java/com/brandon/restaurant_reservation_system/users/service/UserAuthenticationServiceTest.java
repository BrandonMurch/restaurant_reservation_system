/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationServiceTest {


	@Test
	void testCreatePasswordHash() {
		String hash = UserAuthenticationService.createPasswordHash("Password");
		if (hash == null) {
			fail();
		}
		String[] splitHash = hash.split(":");
		assertEquals(3, splitHash.length);
		assertTrue(splitHash[0].matches("\\d+"));
	}

	@Test
	void testValidatePassword() {
		String hash = UserAuthenticationService.createPasswordHash("Password");
		assertTrue(UserAuthenticationService.validatePassword("Password",
		hash));
	}

}
