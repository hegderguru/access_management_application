package com.karur.access_management_application.security.model.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessDetail implements UserDetails {
    private String username;
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
