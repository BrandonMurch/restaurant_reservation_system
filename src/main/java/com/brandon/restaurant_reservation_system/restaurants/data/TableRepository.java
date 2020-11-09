package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TableRepository extends JpaRepository<RestaurantTable,
String> {

	@Override
	@NonNull
	@Query("SELECT t FROM restaurant_table t ORDER BY priority ASC")
	List<RestaurantTable> findAll();
}
