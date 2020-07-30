/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.helpers.json;

import com.brandon.restaurant_reservation_system.users.CreateUsersForTesting;
import com.brandon.restaurant_reservation_system.users.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonConverterTest {

    @Test
    void objectToJson() {
        User user = CreateUsersForTesting.createUser1();
        String userJson = "{\"id\":1,\"email\":\"John.Smith1@email.com\",\"firstName\":\"John\",\"lastName\":\"Smith\",\"phoneNumber\":\"+22 1234567890\",\"termsAndConditions\":true}";
        try {
            String result = JsonConverter.objectToJson(user);
            assertEquals(userJson, result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    void jsonToObject() {
        User user = CreateUsersForTesting.createUser1();
        String userJson = "{\"id\":1,\"email\":\"John.Smith1@email.com\",\"firstName\":\"John\",\"lastName\":\"Smith\",\"phoneNumber\":\"+22 1234567890\",\"termsAndConditions\":true}";
        try {
            User result = JsonConverter.jsonToObject(userJson, User.class);
            assertEquals(user, result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }
    }
}