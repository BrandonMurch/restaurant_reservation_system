/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.model;

import org.springframework.security.core.GrantedAuthority;

public class RequestedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 9113837413736397988L;
    private final String authority;

    public RequestedAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
