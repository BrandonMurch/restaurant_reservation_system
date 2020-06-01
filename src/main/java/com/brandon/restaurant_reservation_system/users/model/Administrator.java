package com.brandon.restaurant_reservation_system.users.model;

public class Administrator extends User {

	private AdminLevel adminLevel;

	public Administrator(String firstName, String lastName, String hash,
	                     String phoneNumber, String email,
	                     AdminLevel adminLevel, boolean termsAndConditions) {
		super(firstName, lastName, hash,
				phoneNumber, email, termsAndConditions);
		this.adminLevel = adminLevel;
	}
}
