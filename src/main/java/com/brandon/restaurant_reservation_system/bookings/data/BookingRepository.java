/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.data;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b " +
	"WHERE  b.startTime < :endTime " +
	"AND b.endTime > :startTime")
	List<Booking> getBookingsDuringTime(
	@Param("startTime") LocalDateTime startTime,
	@Param("endTime") LocalDateTime endTime);

	@Query("SELECT b FROM Booking b WHERE  b.startTime = :startTime")
	List<Booking> getBookingsByStartTime(
	@Param("startTime") LocalDateTime startTime);


	@Query("SELECT b FROM Booking b WHERE  b.startTime >= :date " +
	"and b.startTime < :date2")
	List<Booking> getBookingsBetweenDates(@Param("date") LocalDateTime date,
										  @Param("date2") LocalDateTime date2);

	@Query("SELECT b FROM Booking b INNER JOIN b.user u WHERE username = :username")
	List<Booking> getBookingsByUser(@Param("username") String username);
}
