package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.CombinationOfTables;
import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable,
String> {

	@Override
	@NonNull
	@Query("SELECT t FROM restaurant_table t ORDER BY priority ASC")
	List<RestaurantTable> findAll();

	int deleteByName(String name);

	default int deleteWithAssociatedCombinations(@Param("table") RestaurantTable table) {
		if (table instanceof CombinationOfTables) {
			((CombinationOfTables) table).deleteTables();
		} else {
			List<CombinationOfTables> tables = findAssociatedCombinations(table);
			tables.forEach(CombinationOfTables::deleteTables);
			this.deleteAll(tables);
		}
		return this.deleteByName(table.getName());
	}

	Optional<CombinationOfTables> findCombinationByName(String name);

	@Transactional
	@Modifying
	@Query("UPDATE restaurant_table t " +
	"SET t.priority = :priority, " +
	"t.seats = :seats " +
	"WHERE t.name = :name")
	int updateTable(@Param("name") String name,
					@Param("priority") int priority,
					@Param("seats") int seats);

	default int updateMultipleTables(List<RestaurantTable> tables) {
		return tables
		.stream()
		.mapToInt((table) ->
		updateTable(table.getName(), table.getPriority(), table.getSeats())
		)
		.sum();
	}

	@Query("SELECT MAX(t.seats) FROM restaurant_table t")
	int getLargestTableSize();

	@Query("SELECT t FROM combination_of_tables t WHERE" +
	" COMBINATION = 1 AND :table " +
	"MEMBER OF t.restaurantTables")
	List<CombinationOfTables> findAssociatedCombinations(@Param("table") RestaurantTable table);


	@Query("SELECT t from combination_of_tables t WHERE COMBINATION = 1")
	List<CombinationOfTables> findAllCombinations();
}
