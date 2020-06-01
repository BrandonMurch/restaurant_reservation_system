package com.brandon.restaurant_reservation_system.users.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserAuthenticationServiceTest {
    

    @Test
    void testCreatePasswordHash() {
	String hash = UserAuthenticationService.createPasswordHash("Password");
	String[] splitHash = hash.split(":");
	assertEquals(3, splitHash.length);
	System.out.println(splitHash[0]);
	assertTrue(splitHash[0].matches("\\d+"));
    }
    
    @Test
    void testValidatePassword() {
	String hash = UserAuthenticationService.createPasswordHash("Password");
	assertTrue(UserAuthenticationService.validatePassword("Password", 
		hash));
    }

}
