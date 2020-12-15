package com.brandon.restaurant_reservation_system.errors;

public abstract class ApiSubError {

  public abstract String getObject();

  public abstract void setObject(String object);

  public abstract String getField();

  public abstract void setField(String field);

  public abstract Object getRejectedValue();

  public abstract void setRejectedValue(Object rejectedValue);

  public abstract String getMessage();

  public abstract void setMessage(String message);
}

