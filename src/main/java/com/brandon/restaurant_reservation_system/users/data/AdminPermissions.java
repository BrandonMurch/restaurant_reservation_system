/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.data;

import org.springframework.security.core.GrantedAuthority;

public enum AdminPermissions implements GrantedAuthority {
    VIEW_ADMIN(PermissionType.VIEW, "Admin"),
    VIEW_BOOKINGS(PermissionType.VIEW, "Bookings"),
    EDIT_BOOKINGS(PermissionType.EDIT, "Bookings"),
    VIEW_RESTAURANT(PermissionType.VIEW, "Restaurant"),
    EDIT_RESTAURANT(PermissionType.EDIT, "Restaurant"),
    VIEW_USERS(PermissionType.VIEW, "Users"),
    EDIT_USERS(PermissionType.EDIT, "Users");

    private final PermissionType type;
    private final String target;

    AdminPermissions(PermissionType type, String target) {
        this.type = type;
        this.target = target;
    }

    public PermissionType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public enum PermissionType {
        EDIT, VIEW
    }

    @Override
    public String getAuthority() {
        return this.toString();
    }
}