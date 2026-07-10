package com.karur.access_management_application.security.model.request;

import com.karur.access_management_application.security.compare.DiffId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityRequest {
    @DiffId
    private String name;
    private String description;
}
