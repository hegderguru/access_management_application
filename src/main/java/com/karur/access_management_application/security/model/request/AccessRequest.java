package com.karur.access_management_application.security.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.karur.access_management_application.security.compare.DiffId;
import com.karur.access_management_application.security.compare.IgnoreChange;
import com.karur.access_management_application.security.compare.SecretChange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequest {

    @DiffId
    private String username;

    @SecretChange
    private String password;

    private String firstName;
    private String middleName;

    @IgnoreChange
    private String lastName;
    private Boolean enabled;

    private List<AuthorityRequest> authorityRequests;

}
