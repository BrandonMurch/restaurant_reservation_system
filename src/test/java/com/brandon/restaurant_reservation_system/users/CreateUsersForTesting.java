package com.brandon.restaurant_reservation_system.users;

import com.brandon.restaurant_reservation_system.users.model.User;

public class CreateUsersForTesting {

	public CreateUsersForTesting() {}

	public User createUser1() {
		User user =  new User("John",
			     "Smith",
			     "alkjelkjfeffe",
			     "+22 1234567890",
			     "John.Smith@email.com",true);
		user.setId(1);
		return user;

	}
	public User createUser2() {
		User user = new User("Rachel",
				"Smith",
				"kajfelkafeljke",
				"+22 1234567891",
				"Rachel.Smith@email.com", true);
		user.setId(2);
		return user;
	}
	public User createUpdatedUser2() {
		User user = new User("Rachel",
				"Smith",
				"kajfelkafeljke",
				"+99 38484567891",
				"aNewEmail@email.com", true);
		user.setId(2);
		return user;
	}
	public User createUser3() {
		User user = new User("John", "Smith",
				"alkjelkjfeffe", "+22 1234567890",
				"John.Smith@email.com", true);
		user.setId(3);
		return user;
	}
}
