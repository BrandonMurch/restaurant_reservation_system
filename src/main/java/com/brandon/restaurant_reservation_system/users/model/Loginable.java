/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.model;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ROLE_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Loginable {

    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String username;
    private String password;

    public Loginable(String username, String hash) {
        this.username = username;
        this.password = hash;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Loginable() {
    }
}
