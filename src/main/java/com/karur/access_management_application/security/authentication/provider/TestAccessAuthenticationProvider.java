package com.karur.access_management_application.security.authentication.provider;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Service
@Profile({"local", "test"})
public class TestAccessAuthenticationProvider implements SupportedAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authenticatinClass) {
        return TestingAuthenticationToken.class.isAssignableFrom(authenticatinClass);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            Mono.error(new AuthenticationException("Access is not allowed"));
        }
        TestingAuthenticationToken testingAuthenticationToken = (TestingAuthenticationToken) authentication;
        testingAuthenticationToken.setAuthenticated(true);
        return Mono.just(authentication);
    }
}
