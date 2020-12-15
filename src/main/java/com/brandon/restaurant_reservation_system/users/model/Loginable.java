/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.users.model;

import com.brandon.restaurant_reservation_system.users.data.AdminPermissions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ROLE_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Loginable {

  @Id
  @GeneratedValue
  private long id;
  @Column(unique = true)
  private String username;
  @JsonIgnore
  private String password;
  @ElementCollection(targetClass = AdminPermissions.class)
  @CollectionTable(name = "admin_permissions")
  private Collection<AdminPermissions> permissions;

  public Loginable(String username, String hash, Collection<AdminPermissions> permissions) {
    this.username = username;
    this.password = hash;
    this.permissions = permissions;
  }

  public Loginable() {
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Collection<AdminPermissions> getPermissions() {
    return permissions;
  }

  public void addPermissions(AdminPermissions permission) {
    this.permissions.add(permission);
  }

  public void removePermission(AdminPermissions permission) {
    this.permissions.remove(permission);
  }
}
