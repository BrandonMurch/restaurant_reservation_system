/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.model;

import java.io.Serializable;

public class TokenResponse implements Serializable {

  private static final long serialVersionUID = 2428971056917150283L;
  private final String token;

  public TokenResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
