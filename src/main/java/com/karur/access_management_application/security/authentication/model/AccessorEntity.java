package com.karur.access_management_application.security.authentication.model;

import lombok.Data;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Table(value = "accessor",schema = "creds")
public class AccessorEntity implements UserDetails {

    @Id
    private Long id;

    private String username;
    private String password;
    private boolean accessExpired;
    private boolean accessLocked;
    private boolean accessEnabled;
    private boolean credentialsExpired;

    private String firstName;
    private String middleName;
    private String lastName;

    @Transient
    private List<AccessGrantedAuthority> accessGrantedAuthorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return accessGrantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public List<AccessGrantedAuthority> accessGrantedAuthorities(){
        return accessGrantedAuthorities;
    }
}
