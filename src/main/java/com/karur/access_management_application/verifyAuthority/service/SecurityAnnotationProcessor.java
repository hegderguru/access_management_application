package com.karur.access_management_application.verifyAuthority.service;

import com.karur.access_management_application.verifyAuthority.annotation.VerifyAuthority;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class SecurityAnnotationProcessor {
    public static void processVerification(Object obj) {
        if (obj == null) return;

        // Get all declared fields of the object's class
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            // Check if the field is annotated with @VerifyAuthority
            if (field.isAnnotationPresent(VerifyAuthority.class)) {
                try {
                    field.setAccessible(true); // Bypass private modifier
                    Object value = field.get(obj);

                    if (value != null) {
                        // Check if value matches string "ABC" or integer 123
                        if ("ABC".equals(value) || Integer.valueOf(123).equals(value)) {
                            field.set(obj, null); // Change value to null
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
