package com.brandon.restaurant_reservation_system.users;

import com.brandon.restaurant_reservation_system.users.model.User;

public class CreateUsersForTesting {

	public CreateUsersForTesting() {
	}

	public static User createUser1() {
		User user = new User("John",
		"Smith",
		"alkjelkjfeffe",
		"+22 1234567890",
		"John.Smith1@email.com", true);
		user.setId(1);
		return user;

	}

	public static User createUser2() {
		User user = new User("Rachel",
		"Smith",
		"kajfelkafeljke",
		"+22 1234567891",
		"Rachel.Smith1@email.com", true);
		user.setId(2);
		return user;
	}

	public static User createUpdatedUser2() {
		User user = new User("Rachel",
		"Smith",
		"kajfelkafeljke",
		"+99 38484567891",
		"aNewEmail1@email.com", true);
		user.setId(2);
		return user;
	}

	public User createUser3() {
		User user = new User("John", "Smith",
		"alkjelkjfeffe", "+22 1234567890",
		"John.Smith1@email.com", true);
		user.setId(3);
		return user;
	}

	public static User invalidEmail() {
		User user = new User("John", "Smith",
		"alkjelkjfeffe", "+22 1234567890",
		"this is not an email.com", true);
		user.setId(3);
		return user;
	}

	public static User invalidPhone() {
		User user = new User("John", "Smith",
		"alkjelkjfeffe", "2334349",
		"John.Smith@email.com", true);
		user.setId(3);
		return user;
	}
}
