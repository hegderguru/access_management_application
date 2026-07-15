package com.karur.access_management_application.security.authentication.token;

import com.karur.access_management_application.security.model.read.AuthorityDetail;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String token;

    public JwtAuthenticationToken(String principal, String token, List<AuthorityDetail> authorityDetails) {
        super(authorityDetails);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
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
