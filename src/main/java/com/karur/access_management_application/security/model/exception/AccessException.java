package com.karur.access_management_application.security.model.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Data
public class AccessException {
    HttpStatus httpStatus;
    String message;

    public AccessException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class InvalidCredentialException extends AccessException {
        public InvalidCredentialException(HttpStatus httpStatus, String message, Exception exception) {
            super(httpStatus, getMessage(message, exception));
        }
    }

    public static String getMessage(String message, Exception exception) {
        if (Objects.isNull(exception)) {
            return message;
        }
        return message + ":" + exception.getMessage() + ":" + exception.getCause();
    }
}
