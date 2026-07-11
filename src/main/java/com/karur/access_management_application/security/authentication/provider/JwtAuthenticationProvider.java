package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.authentication.jwt.JwtTokenProvider;
import com.karur.access_management_application.security.authentication.token.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
        if(authentication instanceof JwtAuthenticationToken){
            String token = (String) authentication.getCredentials();
            if(jwtTokenProvider.validateToken(token)){
                String username = jwtTokenProvider.getUsernameFromToken(token);
                return Mono.just(new JwtAuthenticationToken(token,username));
            }
        }
        return Mono.empty();
    }
}
