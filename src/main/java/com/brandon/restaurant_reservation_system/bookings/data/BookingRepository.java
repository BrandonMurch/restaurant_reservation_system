/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.bookings.data;

import com.brandon.restaurant_reservation_system.bookings.model.Booking;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@Query("SELECT b FROM Booking b INNER JOIN b.restaurantTables t " +
	"WHERE t.name = :table_name " +
	"AND b.startTime > CURRENT_TIMESTAMP")
	List<Booking> getFutureBookingsByTable(@Param("table_name") String tableName);

	@Query("SELECT b FROM Booking b INNER JOIN b.restaurantTables t " +
	"WHERE  b.startTime < :endTime " +
	"AND b.endTime > :startTime "
	+
	"AND t.name = :tableName"
	)
	List<Booking> getBookingsByTimeAndTable(
	@Param("startTime") LocalDateTime startTime,
	@Param("endTime") LocalDateTime endTime,
	@Param("tableName") String tableName
	);

	default Set<Booking> getBookingsByTimeAndMultipleTables(LocalDateTime startTime,
															LocalDateTime endTime,
															List<RestaurantTable> tables) {
		HashSet<Booking> bookings = new HashSet<>();
		tables.forEach((table) -> bookings.addAll(getBookingsByTimeAndTable(
		startTime,
		endTime,
		table.getName()
		)));
		return bookings;
	}


	@Query("SELECT date, sum(b.partySize) FROM Booking b GROUP BY b.date")
	List<Object[]> getCountByDay();

	default HashMap<LocalDate, Integer> getCountByDayMap() {
		HashMap<LocalDate, Integer> map = new HashMap<>();
		getCountByDay().forEach(object -> {
			LocalDate date = LocalDate.parse(String.valueOf(object[0]));
			Integer count = object[1] == null ? null : Integer.valueOf(String.valueOf(object[1]));
			map.put(date, count);
		});
		return map;
	}
}
