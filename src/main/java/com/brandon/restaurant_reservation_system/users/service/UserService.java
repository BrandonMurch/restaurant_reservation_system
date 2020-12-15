/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.service;

import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.model.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public UserService() {
  }

  public User createUserInDBIfNotAlreadyPresent(User user) {
    Optional<User> dbUser = userRepository.findByEmail(user.getEmail());

    if (dbUser.isEmpty()) {
      userRepository.save(user);
      return user;
    }
    return dbUser.get();
  }

}
