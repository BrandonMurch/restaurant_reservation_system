package com.brandon.restaurant_reservation_system.users.data;

import com.brandon.restaurant_reservation_system.users.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE  u.username = :username")
  Optional<User> findByEmail(
      @Param("username") String username);
}
