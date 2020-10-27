/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.model;

import com.brandon.restaurant_reservation_system.helpers.date_time.services.*;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.formatDateTime;

@Entity
public class Booking implements Cloneable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Integer partySize;
	private String userComments;
	private String restaurantComments;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate date;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime startTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime endTime;
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name = "bookings_table",
	joinColumns = @JoinColumn(name = "bookings_id"),
	inverseJoinColumns = @JoinColumn(name = "restaurantTable_id"))
	private List<RestaurantTable> restaurantTables;

	public Booking(int partySize, LocalDateTime startTime,
				   LocalDateTime endTime, User user) {
		this(partySize, startTime, endTime, user, "", "");
	}

	public Booking(int partySize, LocalDateTime startTime,
				   LocalDateTime endTime, User user, String userComments,
				   String restaurantComments) {
		this.partySize = partySize;
		this.startTime = startTime;
		date = startTime.toLocalDate();
		this.endTime = endTime;
		this.user = user;
		this.userComments = userComments;
		this.restaurantComments = restaurantComments;
	}

	public Booking() {
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getPartySize() {
		return partySize;
	}

	public void setPartySize(Integer partySize) {
		this.partySize = partySize;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserComments() {
		return userComments;
	}

	public void setUserComments(String userComments) {
		this.userComments = userComments;
	}

	public String getRestaurantComments() {
		return restaurantComments;
	}

	public void setRestaurantComments(String restaurantComments) {
		this.restaurantComments = restaurantComments;
	}

	public void updateBooking(Booking newBooking) {
		Integer partySize = newBooking.getPartySize();
		if (partySize != null) {
			this.setPartySize(partySize);
		}
		LocalDateTime startTime = newBooking.getStartTime();
		if (startTime != null) {
			this.setStartTime(startTime);
		}
		LocalDateTime endTime = newBooking.getEndTime();
		if (endTime != null) {
			this.setEndTime(endTime);
		}

		User user = newBooking.getUser();
		if (user != null) {
			this.setUser(user);
		}

		String comment = newBooking.getRestaurantComments();
		if (comment != null) {
			this.setRestaurantComments(comment);
		}

		comment = newBooking.getUserComments();
		if (comment != null) {
			this.setUserComments(comment);
		}
	}

	public List<RestaurantTable> getTables() {
		return restaurantTables;
	}

	public void setTables(List<RestaurantTable> restaurantTables) {
		if (restaurantTables != null) {
			this.restaurantTables = new ArrayList<>(restaurantTables);

		}
	}

	public void addTable(RestaurantTable restaurantTable) {
		if (this.restaurantTables == null) {
			this.restaurantTables = new ArrayList<>();
		}
		this.restaurantTables.add(restaurantTable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getStartTime(), getEndTime());
	}

	public boolean doTheseBookingsOverlap(Booking otherBooking) {
		return isTheBookingDuringThisTime(otherBooking.getStartTime(),
				otherBooking.getEndTime());
	}

		public boolean isTheBookingDuringThisTime(LocalDateTime startTime,
		                                      LocalDateTime endTime) {
			if (this.getStartTime().isAfter(endTime)
					|| this.getStartTime().isEqual(endTime)) {
				return false;
			}
			return !(this.getEndTime().isBefore(startTime)
					|| this.getEndTime().isEqual(startTime));
		}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Booking)) return false;
		Booking booking = (Booking) o;
		return getId() == booking.getId() &&
		Objects.equals(getStartTime(), booking.getStartTime()) &&
		Objects.equals(getEndTime(), booking.getEndTime()) &&
		Objects.equals(getPartySize(), booking.getPartySize());
	}

	public Booking clone() throws CloneNotSupportedException {
		return (Booking) super.clone();
	}

	@Override
	public String toString() {


		String date = formatDateTime(startTime,
		CustomDateTimeFormatter.READABLE_DATE);
		String time = formatDateTime(startTime,
		CustomDateTimeFormatter.TIME_NO_SECONDS);

		return "Booking for " + partySize + " on " + date + " at " + time + ".";
	}
}
