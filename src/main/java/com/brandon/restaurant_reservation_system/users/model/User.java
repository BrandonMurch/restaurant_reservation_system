/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@DiscriminatorValue("USER")
public class User extends Loginable {

	@Id
	@GeneratedValue
	private long id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private boolean termsAndConditions;


	public User() {
		super();
	}

	public User(User newUser) {
		super(newUser.getUsername(), newUser.getPassword(), new ArrayList<>());
		this.firstName = newUser.firstName;
		this.lastName = newUser.lastName;
		this.phoneNumber = newUser.phoneNumber;
		this.termsAndConditions = newUser.termsAndConditions;
		this.id = newUser.id;
	}

	public User(String firstName, String lastName, String password,
				String phoneNumber, String email, boolean termsAndConditions) {
		super(email, password, new ArrayList<>());
		this.firstName = firstLetterToUppercase(firstName);
		this.lastName = firstLetterToUppercase(lastName);
		this.phoneNumber = phoneNumber;
		this.termsAndConditions = termsAndConditions;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {

		this.id = id;
	}

	public void updateWith(User updatedUser) {
		String phone = updatedUser.getPhoneNumber();
		if (phone != null) {
			this.setPhoneNumber(phone);
		}
	}

	@JsonIgnore
	public String getEmail() {
		return this.getUsername();
	}

	public String getPhoneNumber() {

		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;

	}

	public boolean getTermsAndConditions() {
		return termsAndConditions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getUsername() == null) ? 0 :
		this.getUsername().hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		String username = this.getUsername();
		String otherUserName = other.getUsername();
		if (username == null) {
			if (otherUserName != null)
				return false;
		} else if (!username.equals(otherUserName))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (phoneNumber == null) {
			return other.phoneNumber == null;
		} else return phoneNumber.equals(other.phoneNumber);
	}

	@Override
	public String toString() {
		return "User{" +
		" firstName='" + firstName + '\'' +
		", lastName='" + lastName + '\'' +
		'}';
	}

	protected String firstLetterToUppercase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
