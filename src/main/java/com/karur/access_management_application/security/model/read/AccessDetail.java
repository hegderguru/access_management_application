package com.karur.access_management_application.security.model.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessDetail {
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean accessEnabled;
    private boolean accessLocked;
    private boolean accessExpired;
    private boolean credentialsExpired;
    private List<AuthorityDetail> authorityDetails;
}
