/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.security.model;

public class PageAuthorizationRequest {

    private String token;
    private String permission;

    // TODO,
    public PageAuthorizationRequest(String token, String permission) {
        this.token = token;
        this.permission = permission;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPermission() {
        return permission;
    }

    public void getPermission(String page) {
        this.permission = page;
    }
}
