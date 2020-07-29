/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.model;

import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserTest {

	User user1;
	private UserPasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		user1 = new User("Albert", "Smith",
		passwordEncoder.encode("Password"),
		"1234567894", "Albert.Smith@email.com", true);
		userRepository.save(user1);
	}

	@Test
	void testHashCode() {
		User user2 = user1;
		assertEquals(user1.hashCode(), user2.hashCode());
	}


	@Test
	void testGetFirstName() {
		assertEquals("Albert", user1.getFirstName());
	}

	@Test
	void testGetLastName() {
	assertEquals("Smith", user1.getLastName());
    }

    @Test
    void testGetPhoneNumber() {
	assertEquals("1234567894", user1.getPhoneNumber());	
    }

    @Test
    void testSetPhoneNumber() {
	user1.setPhoneNumber("1234567895");
	assertEquals("1234567895", user1.getPhoneNumber());	
    }

	@Test
	void testGetEmail() {
		assertEquals("Albert.Smith@email.com", user1.getEmail());
	}

	@Test
	void testGetId() {
		assertTrue(user1.getId() > 0);
	}

	@Test
	void firstLetterToUppercase() {
		String word = "john";
		String expected = "John";
		String actual = new User("john", "johnson", null, "+1 12345678", "email@email" +
		".com", true).getFirstName();

		assertEquals(expected, actual);
	}
}
