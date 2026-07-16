package com.karur.access_management_application.security.model.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karur.access_management_application.security.compare.SecretChange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessDetail{
    private String username;
    @SecretChange
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean accessEnabled;
    private Boolean accessLocked;
    private Boolean accessExpired;
    private Boolean credentialsExpired;
    private List<AuthorityDetail> authorityDetails;
}
