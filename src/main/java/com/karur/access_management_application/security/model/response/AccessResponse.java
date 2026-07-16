package com.karur.access_management_application.security.model.response;

import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccessResponse {

    HttpStatus httpStatus;
    String message;
    AccessDetail accessDetail;
    AuthorityDetail authorityDetail;
    RoleDetail roleDetail;

    public AccessResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public AccessResponse(HttpStatus httpStatus, String message, AccessDetail accessDetail) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.accessDetail = accessDetail;
    }

    public AccessResponse(HttpStatus httpStatus, String message, AuthorityDetail authorityDetail) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.authorityDetail = authorityDetail;
    }

    public AccessResponse(HttpStatus httpStatus, String message, RoleDetail roleDetail) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.roleDetail = roleDetail;
    }

}
