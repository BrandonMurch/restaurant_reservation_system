/*
 *  https://www.javainuse.com/spring/boot-jwt
 */

package com.brandon.restaurant_reservation_system.security.service;

import com.brandon.restaurant_reservation_system.security.model.UserRegisterRequest;
import com.brandon.restaurant_reservation_system.users.data.LoginableRepository;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.exceptions.UserNotFoundException;
import com.brandon.restaurant_reservation_system.users.model.Loginable;
import com.brandon.restaurant_reservation_system.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private LoginableRepository loginableRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<Loginable> result = loginableRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new UserNotFoundException("User was not found");
        } else {
            Loginable user = result.get();
            return new org.springframework.security.core.userdetails.User(
              user.getUsername(), user.getPassword(), user.getPermissions()
            );
        }
    }

    // TODO: move this to user handler?
    public User saveUser(UserRegisterRequest newUser) {
        User user = new User(
          newUser.getFirstName(),
          newUser.getLastName(),
          passwordEncoder.encode(newUser.getPassword()),
          newUser.getPhoneNumber(),
          newUser.getUsername(),
          newUser.isTermsAndConditions()
        );
        return userRepository.save(user);
    }
}
