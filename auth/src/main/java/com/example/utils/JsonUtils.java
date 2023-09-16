package com.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonUtils {

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object asObject(final String jsonString, Class<?> className){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, className);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
