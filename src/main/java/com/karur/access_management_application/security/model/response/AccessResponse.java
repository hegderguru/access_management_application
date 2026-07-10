package com.karur.access_management_application.security.model.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AccessResponse {

    HttpStatus httpStatus;
    String message;

    public AccessResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
