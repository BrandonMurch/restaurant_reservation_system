package com.brandon.restaurant_reservation_system.restaurants.data;

import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface TableRepository extends JpaRepository<RestaurantTable,
    String> {

  @Override
  @NonNull
  @Query("SELECT t FROM restaurant_table t ORDER BY priority ASC")
  List<RestaurantTable> findAll();

  default void deleteWithAssociatedCombinations(@Param("table") RestaurantTable table) {
    int count = 0;
    table.removeTables();
    removeAssociatedCombinations(table);
    delete(table);
  }

  private void removeAssociatedCombinations(RestaurantTable table) {
    List<RestaurantTable> tables = findAssociatedCombinations(table);
    tables.forEach(RestaurantTable::removeTables);
    deleteAll(tables);
  }

  @Query("SELECT MAX(t.seats) FROM restaurant_table t")
  int getLargestTableSize();

  @Query("SELECT t FROM restaurant_table t WHERE" +
      " :table MEMBER OF t.tables")
  List<RestaurantTable> findAssociatedCombinations(@Param("table") RestaurantTable table);
}
