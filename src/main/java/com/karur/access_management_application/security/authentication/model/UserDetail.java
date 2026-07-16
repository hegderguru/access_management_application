package com.karur.access_management_application.security.authentication.model;


import com.karur.access_management_application.security.model.read.AccessDetail;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class UserDetail implements UserDetails {

    private String username;
    private String password;
    private boolean accessEnabled;
    private boolean accessLocked;
    private boolean accessExpired;
    private boolean credentialsExpired;
    private List<UserAuthority> authorities;

    public UserDetail(AccessDetail accessDetail) {
        username = accessDetail.getUsername();
        password = accessDetail.getPassword();
        accessEnabled = accessDetail.isAccessEnabled();
        accessLocked = accessDetail.isAccessLocked();
        accessExpired = accessDetail.isAccessExpired();
        credentialsExpired = accessDetail.isCredentialsExpired();
        authorities = accessDetail.getAuthorityDetails().stream()
                .map(authorityDetail -> new UserAuthority(authorityDetail.getName())).toList();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accessExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accessLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return accessEnabled;
    }

    @Override
    public List<UserAuthority> getAuthorities() {
        return authorities;
    }
}
