package com.karur.access_management_application.security.authentication.token;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class GitHubTokenAuthenticationToken extends AbstractAuthenticationToken {

    private String username;

    @Getter
    private String token;

    public GitHubTokenAuthenticationToken(String username, String token) {
        super(Collections.emptyList());
        this.username = username;
        this.token = token;
        setAuthenticated(false);
    }

    public GitHubTokenAuthenticationToken(String username, String token, @Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.token = token;
        setAuthenticated(false);
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return null;
    }


    @Override
    public String getName() {
        return super.getName();
    }

}
