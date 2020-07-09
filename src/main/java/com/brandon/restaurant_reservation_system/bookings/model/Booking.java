package com.brandon.restaurant_reservation_system.bookings.model;

import com.brandon.restaurant_reservation_system.helpers.date_time.services.CustomDateTimeFormatter;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.LocalDateTimeDeserializer;
import com.brandon.restaurant_reservation_system.helpers.date_time.services.LocalDateTimeSerializer;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brandon.restaurant_reservation_system.helpers.date_time.services.DateTimeHandler.formatDateTime;

@Entity
public class Booking {
	@Id
	@GeneratedValue
	private long id;
	private Integer partySize;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime startTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime endTime;
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "bookings_table",
			joinColumns = @JoinColumn(name = "bookings_id"),
			inverseJoinColumns = @JoinColumn(name = "restaurantTable_id"))
	private List<RestaurantTable> restaurantTables;

	public Booking(int partySize, LocalDateTime startTime,
	               LocalDateTime endTime, User user) {
		this.partySize = partySize;
		this.startTime = startTime;
		this.endTime = endTime;
		this.user = user;
	}

	public Booking() {
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
	}

	public List<RestaurantTable> getTable() {
		return restaurantTables;
	}

	public void setTable(List<RestaurantTable> restaurantTables) {
		this.restaurantTables = restaurantTables;
	}

	public void addTable(RestaurantTable restaurantTable) {
		if (this.restaurantTables == null) {
			this.restaurantTables = new ArrayList<>();
		}
		this.restaurantTables.add(restaurantTable);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Booking)) return false;
		Booking booking = (Booking) o;
		return getId() == booking.getId() &&
				Objects.equals(getStartTime(), booking.getStartTime()) &&
				Objects.equals(getEndTime(), booking.getEndTime()) &&
				Objects.equals(getUser(), booking.getUser());
	}

	public boolean doTheseBookingsOverlap(Booking otherBooking) {
		return isTheBookingDuringThisTime(otherBooking.getStartTime(),
				otherBooking.getEndTime());
	}

		public boolean isTheBookingDuringThisTime(LocalDateTime startTime,
		                                      LocalDateTime endTime) {
			if (this.getStartTime().isAfter(endTime)) {
				return true;
			} else return this.getEndTime().isBefore(startTime);
		}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getStartTime(), getEndTime(), getUser());
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
