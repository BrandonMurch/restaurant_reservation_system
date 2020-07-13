package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinationRepository
		extends JpaRepository<CombinationOfTables, String> {
}
