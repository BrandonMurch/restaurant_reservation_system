package com.brandon.restaurant_reservation_system.users.data;

import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { }
