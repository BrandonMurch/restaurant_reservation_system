package com.brandon.restaurant_reservation_system.users.model;

import com.brandon.restaurant_reservation_system.users.data.AdminPermissions;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/*
	reference Administrator to see if they have permissions to do something
 */

@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends Loginable {

	private Administrator(String username, String hash, Collection<AdminPermissions> permissions) {
		super(username, hash, permissions);
	}

	private Administrator() {
	}

	@Override
	public String toString() {
		return this.getUsername();
	}

	public static class AdminBuilder {
		private long id;
		private final String username;
		private String hash;
		private Set<AdminPermissions> permissions;

		public AdminBuilder(String username) {
			this.username = username;
		}

		public AdminBuilder addPassword(String password) {
			PasswordEncoder passwordEncoder = new UserPasswordEncoder();
			this.hash = passwordEncoder.encode(password);

			return this;
		}

		public AdminBuilder addAlreadyHashedPassword(String password) {
			this.hash = password;

			return this;
		}

		public Administrator buildNoPrivilegeAdmin() {
			this.permissions = new HashSet<>();
			return new Administrator(username, hash, permissions);
		}

		public Administrator buildViewOnlyAdmin() {
			permissions = Arrays.stream(AdminPermissions.values())
			.filter(permission ->
			permission.getType().equals(AdminPermissions.PermissionType.VIEW)
			)
			.collect(Collectors.toSet());
			return new Administrator(username, hash, permissions);

		}

		public Administrator buildFullAdmin() {
			permissions =
			Arrays.stream(AdminPermissions.values()).collect(Collectors.toSet());
			return new Administrator(username, hash, permissions);
		}
	}

}
