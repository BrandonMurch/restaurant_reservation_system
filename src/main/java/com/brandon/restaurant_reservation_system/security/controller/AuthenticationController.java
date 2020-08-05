/*
 *  https://www.javainuse.com/spring/boot-jwt
 */
package com.brandon.restaurant_reservation_system.security.controller;

import com.brandon.restaurant_reservation_system.security.model.*;
import com.brandon.restaurant_reservation_system.security.service.JwtTokenUtil;
import com.brandon.restaurant_reservation_system.security.service.JwtUserDetailsService;
import com.brandon.restaurant_reservation_system.users.data.UserRepository;
import com.brandon.restaurant_reservation_system.users.service.UserPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private final UserPasswordEncoder passwordEncoder = new UserPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil tokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserRegisterRequest newUser) {
        return ResponseEntity.ok(userDetailsService.saveUser(newUser));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
      @RequestBody PageAuthorizationRequest authorizationRequest,
      HttpServletRequest request
    ) {

        String token = authorizationRequest.getToken();
        if (!tokenUtil.validateToken(authorizationRequest.getToken(),
          request.getRemoteAddr())) {
            return ResponseEntity.status(401).build();
        }

        String username = tokenUtil.getUsernameFromToken(token);
        RequestedAuthority permission =
          new RequestedAuthority(authorizationRequest.getPermission());
        // TODO: add permissions to token
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user.getAuthorities().contains(permission)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> generateAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest,
      HttpServletRequest request
    ) throws Exception {

        authenticate(authenticationRequest.getUsername(),
          authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
          .loadUserByUsername(authenticationRequest.getUsername());

        final String token = tokenUtil.generateToken(userDetails, request.getRemoteAddr());

        return ResponseEntity.ok(new TokenResponse(token));

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager
              .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException error) {
            throw new Exception("USER_DISABLED", error);
        } catch (BadCredentialsException error) {
            throw new Exception("INVALID_CREDENTIALS", error);
        }
    }
}
