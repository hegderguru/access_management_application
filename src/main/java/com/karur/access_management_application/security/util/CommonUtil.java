package com.karur.access_management_application.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

    public static String writeValueAsString(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objectMapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
