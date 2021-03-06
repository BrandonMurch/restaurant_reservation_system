/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.data;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Cache<T> {

  private final Lock lock = new ReentrantLock();
  protected T data;
  private LocalDate datesLastUpdated;

  public Cache(T data) {
    this.data = data;
    // Causes the cache to update upon first get();
    datesLastUpdated = LocalDate.now().minusDays(1);
  }

  protected abstract T update();

  public T get() {
    return handleLock(() -> {
      if (!datesLastUpdated.isEqual(LocalDate.now())) {
        data = update();
        datesLastUpdated = LocalDate.now();
      }
      return data;
    });
  }

  protected T getForUpdate() {
    return data;
  }

  public void set(T data) {
    handleLock(() -> {
      this.data = data;
    });
  }

  protected void handleLock(Runnable function) {
    try {
      lock.lock();
      function.run();
    } finally {
      lock.unlock();
    }
  }

  protected T handleLock(Callable<T> callable) {
    try {
      lock.lock();
      return callable.call();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      lock.unlock();
    }
  }
}
