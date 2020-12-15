/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.data;

import com.brandon.restaurant_reservation_system.users.model.Administrator;
import com.brandon.restaurant_reservation_system.users.model.Loginable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginableRepository extends JpaRepository<Loginable, Long> {

  Optional<Loginable> findByUsername(
      @Param("username") String username);

  @Query(value = "SELECT * from loginable WHERE ROLE_TYPE = 'ADMINISTRATOR'",
      nativeQuery = true)
  List<Administrator> findAllAdmins();

}
