package com.karur.access_management_application.security.model.request;

import com.karur.access_management_application.security.change.ChangeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityRequest {
    @ChangeId
    private String name;
    private String description;
}
