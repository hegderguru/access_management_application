package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.authentication.entity.AuthorityEntity;
import com.karur.access_management_application.security.authentication.token.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class JwtAuthenticationProvider implements SupportedAuthenticationProvider {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supports(Class<?> authenticatinClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(authenticatinClass);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            String token = (String) authentication.getCredentials();
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                List<String> authorities = jwtTokenProvider.getAuthorities(token);
                List<AuthorityEntity> grantedAuthorityEntities = authorities.stream().map(authority -> AuthorityEntity.builder().name(authority).build()).toList();
                return Mono.just(new JwtAuthenticationToken(token, username, grantedAuthorityEntities));
            }
        }
        return Mono.empty();
    }
}
