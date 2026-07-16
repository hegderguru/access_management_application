package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.authentication.token.JwtAuthenticationToken;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.UserAuthority;
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
                List<UserAuthority> userAuthorities = authorities.stream().map(UserAuthority::new).toList();
                return Mono.just(new JwtAuthenticationToken(username,token, userAuthorities));
            }
        }
        return Mono.empty();
    }
}
