package com.karur.access_management_application.security.model.read;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.karur.access_management_application.security.compare.SecretChange;
import com.karur.access_management_application.verifyAuthority.annotation.VerifyAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessDetail implements UserDetails {

    private String username;

    @SecretChange
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean accessEnabled;
    private boolean accessLocked;
    private boolean accessExpired;
    private boolean credentialsExpired;
    private List<AuthorityDetail> authorities;

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public List<AuthorityDetail> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccessExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccessLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return isAccessEnabled();
    }
}
