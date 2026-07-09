package com.karur.access_management_application.security.authentication.token;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String token;

    public JwtAuthenticationToken(String principal,String token){
        super(Collections.emptyList());
        this.principal=principal;
        this.token=token;
        setAuthenticated(false);
    }

    @Override
    public @Nullable Object getCredentials() {
        return token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }
}
