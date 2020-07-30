/*
 *  https://www.javainuse.com/spring/boot-jwt
 */

package com.brandon.restaurant_reservation_system.security;

import com.brandon.restaurant_reservation_system.security.service.JwtTokenUtil;
import com.brandon.restaurant_reservation_system.security.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
      HttpServletResponse response,
      FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
            try {
                username = tokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException error) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException error) {
                System.out.println("JWT token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (username != null
          && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails =
              this.userDetailsService.loadUserByUsername(username);

            if (tokenUtil.validateToken(token, userDetails)) {
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
