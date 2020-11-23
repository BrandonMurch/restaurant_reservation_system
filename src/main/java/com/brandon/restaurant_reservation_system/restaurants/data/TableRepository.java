package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable,
String> {

	@Override
	@NonNull
	@Query("SELECT t FROM restaurant_table t ORDER BY priority ASC")
	List<RestaurantTable> findAll();

	void deleteByName(String name);

	default void deleteWithAssociatedCombinations(@Param("table") RestaurantTable table) {
		int count = 0;
		if (table instanceof CombinationOfTables) {
			((CombinationOfTables) table).deleteTables();
		} else {
			List<CombinationOfTables> tables = findAssociatedCombinations(table);
			tables.forEach(CombinationOfTables::deleteTables);
			this.deleteAll(tables);
		}
		this.deleteByName(table.getName());
	}

	Optional<CombinationOfTables> findCombinationByName(String name);

	@Query("SELECT MAX(t.seats) FROM restaurant_table t")
	int getLargestTableSize();

	@Query("SELECT t FROM combination_of_tables t WHERE" +
	" COMBINATION = 1 AND :table " +
	"MEMBER OF t.restaurantTables")
	List<CombinationOfTables> findAssociatedCombinations(@Param("table") RestaurantTable table);


	@Query("SELECT t from combination_of_tables t WHERE COMBINATION = 1")
	List<CombinationOfTables> findAllCombinations();
}
