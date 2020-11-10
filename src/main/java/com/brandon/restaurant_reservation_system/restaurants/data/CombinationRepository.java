package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CombinationRepository
extends JpaRepository<CombinationOfTables, String> {

	@Override
	@NonNull
	@Query("SELECT t FROM combination_of_tables t ORDER BY priority ASC")
	List<CombinationOfTables> findAll();

}
