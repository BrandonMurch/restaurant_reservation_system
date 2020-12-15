/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.restaurants.services;

import com.brandon.restaurant_reservation_system.restaurants.model.RestaurantTable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class RestaurantTableDeserializer extends
    JsonDeserializer<List<RestaurantTable>> {

  @Override
  public List<RestaurantTable> deserialize(JsonParser p, DeserializationContext ctx)
      throws IOException {
    String str = p.getText();
    // TODO: REMOVE ME
    System.out.println("********************************************");
    System.out.println("\n \n \n \n");
    System.out.println(p.getTypeId());
    System.out.println("\n \n \n \n");
    System.out.println("********************************************");
    ObjectMapper mapper = new ObjectMapper();
    JavaType classCollection = mapper.getTypeFactory()
        .constructCollectionType(List.class, RestaurantTable.class);
    try {
      return mapper.readValue(str, classCollection);
    } catch (Exception ex) {
      System.out.println(ex);
    }

    return null;
  }
}
