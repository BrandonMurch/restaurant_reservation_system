/*
 *  https://www.javainuse.com/spring/boot-jwt
 */

package com.brandon.restaurant_reservation_system.security;

import com.brandon.restaurant_reservation_system.security.service.JwtTokenUtil;
import com.brandon.restaurant_reservation_system.security.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Profile("!Test")
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUserDetailsService userDetailsService;
  @Autowired
  private JwtTokenUtil tokenUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull
          HttpServletResponse response,
      @NonNull
          FilterChain filterChain
  ) throws ServletException, IOException {
    final String requestTokenHeader = request.getHeader("Authorization");
    String username = null;
    String token = null;

    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      token = requestTokenHeader.substring(7);
      try {
        username = tokenUtil.getUsernameFromToken(token);
      } catch (IllegalArgumentException | ExpiredJwtException ignored) {
      }
    }

    if (username != null
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails =
          this.userDetailsService.loadUserByUsername(username);

      if (tokenUtil.validateTokenWithUser(token, userDetails, request.getRemoteAddr())) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
            = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
        SecurityContextHolder.getContext()
            .setAuthentication(usernamePasswordAuthenticationToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
