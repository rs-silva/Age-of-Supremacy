package com.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

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

    public static List<Object> asObjectList(final String jsonString, Class<?> className){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, className));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
