/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue
	private long id;
	@Column(unique = true)
	private String email;
	private String firstName;
	private String lastName;
	@JsonIgnore
	private String hash;
	private String phoneNumber;
	private boolean termsAndConditions;


	public User() {
	}

	public User(User newUser) {
		this(
		newUser.getFirstName(),
		newUser.getLastName(),
		newUser.getHash(),
		newUser.getPhoneNumber(),
		newUser.getEmail(),
		newUser.getTermsAndConditions()
		);
		this.id = newUser.id;

	}

	public User(String firstName, String lastName, String hash,
				String phoneNumber, String email, boolean termsAndConditions) {
		this.firstName = firstLetterToUppercase(firstName);
		this.lastName = firstLetterToUppercase(lastName);
		this.hash = hash;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.termsAndConditions = termsAndConditions;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getHash() {
		return hash;
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

	public String getEmail() {
		return email;
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
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
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
		"id=" + id +
		", firstName='" + firstName + '\'' +
		", lastName='" + lastName + '\'' +
		'}';
	}

	protected String firstLetterToUppercase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
