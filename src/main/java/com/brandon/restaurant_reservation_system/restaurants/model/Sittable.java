/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Sittable {
    @Id
    private String name;
    private int seats;
    private int priority;

    public Sittable() {
    }

    public Sittable(String name, int seats, int priority) {
        this.name = name;
        this.seats = seats;
        this.priority = priority;
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getSeats() {
        return this.seats;
    }

    protected void setSeats(int seats) {
        this.seats = seats;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
