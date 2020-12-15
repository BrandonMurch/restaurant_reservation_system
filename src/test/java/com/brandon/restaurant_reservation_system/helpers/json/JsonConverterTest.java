/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class JsonConverterTest {


  @Test
  void objectToJson() {
    ObjectForTest testObject = new ObjectForTest();
    testObject.setId(4);
    testObject.setName("john");
    String expected = "{\"id\":4,\"name\":\"john\"}";
    try {
      String result = JsonConverter.objectToJson(testObject);
      assertEquals(expected, result);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      fail();
    }

  }

  @Test
  void jsonToObject() {
    ObjectForTest testObject = new ObjectForTest();
    testObject.setId(4);
    testObject.setName("john");
    String json = "{\"id\":4,\"name\":\"john\"}";
    try {
      ObjectForTest result = JsonConverter.jsonToObject(json, ObjectForTest.class);
      assertEquals(testObject, result);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      fail();
    }
  }


  private static class ObjectForTest {

    private long id;
    private String name;

    public ObjectForTest() {

    }

    public ObjectForTest(long id, String name) {
      this.id = id;
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ObjectForTest that = (ObjectForTest) o;
      return getId() == that.getId() &&
          Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getId(), getName());
    }
  }
}