package com.karur.access_management_application.security.model.read;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class UserAuthority implements GrantedAuthority {

    private String authority;

    @Override
    public @Nullable String getAuthority() {
        return authority;
    }
}