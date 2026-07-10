package com.karur.access_management_application.security.model.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class AccessResponse {

    HttpStatus httpStatus;
    String message;

    public AccessResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
