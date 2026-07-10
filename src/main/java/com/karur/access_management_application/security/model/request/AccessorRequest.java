package com.karur.access_management_application.security.model.request;

import com.karur.access_management_application.security.change.ChangeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessorRequest {

    @ChangeId
    private String username;

    private String firstName;
    private String middleName;
    private String lastName;
    private boolean enabled;

    private List<AuthorityRequest> authorityRequests;

}
