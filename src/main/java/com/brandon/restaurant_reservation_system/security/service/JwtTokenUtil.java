/*
 *  https://www.javainuse.com/spring/boot-jwt
 */

package com.brandon.restaurant_reservation_system.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {

  public static final long JWT_TOKEN_VALIDITY = 20 * 60 * 60;
  private static final long serialVersionUID = -4600787903482131864L;
  @Value("${jwt.secret}")
  private String secret;

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public String getIpAddressFromToken(String token) {
    Function<Claims, String> getIpAddress = claims -> claims.get("ip_address",
        String.class);
    return getClaimFromToken(token, getIpAddress);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) throws SignatureException {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(UserDetails userDetails, String requestAddress) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("ip_address", requestAddress);
    return doGenerateToken(claims, userDetails.getUsername());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Boolean validateTokenWithUser(String token, UserDetails userDetails,
      String requestAddress) {
    final String username;
    try {
      username = getUsernameFromToken(token);
    } catch (SignatureException e) {
      return false;
    }
    final String ipAddress = getIpAddressFromToken(token);
    return (username.equals(userDetails.getUsername())
        && !isTokenExpired(token))
        && ipAddress.equals(requestAddress);
  }

  public Boolean validateToken(String token, String address) {
    try {
      return !isTokenExpired(token) && address.equals(getIpAddressFromToken(token));
    } catch (SignatureException e) {
      return false;
    }
  }


}
