package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TableRepository extends JpaRepository<RestaurantTable,
String> {

	@Override
	@NonNull
	@Query("SELECT t FROM restaurant_table t ORDER BY priority ASC")
	List<RestaurantTable> findAll();

	@Query(value = "SELECT * from restaurant_table WHERE ROLE_TYPE = " +
	"'COMBINATION_OF_TABLES'",
	nativeQuery = true)
	List<CombinationOfTables> findAllCombinations();

	default void deleteWithAssociatedCombinations(@Param("table") RestaurantTable table) {
		List<CombinationOfTables> tables = getAssociatedCombinations(table);
		tables.forEach(CombinationOfTables::deleteTables);
		this.deleteAll(tables);
		this.delete(table);
	}

	@Query("SELECT t from combination_of_tables t WHERE" +
	" COMBINATION = 1 AND :table " +
	"MEMBER OF t.restaurantTables")
	List<CombinationOfTables> getAssociatedCombinations(@Param("table") RestaurantTable table);


	@Query("SELECT t from combination_of_tables t WHERE COMBINATION = 1")
	List<CombinationOfTables> getAllCombinations();
}
