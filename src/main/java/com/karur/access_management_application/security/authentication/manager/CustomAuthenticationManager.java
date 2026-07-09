package com.karur.access_management_application.security.authentication.manager;

import com.karur.access_management_application.security.authentication.provider.SupportedAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final List<SupportedAuthenticationProvider> supportedAuthenticationProviders;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return supportedAuthenticationProviders.stream().filter(supportedAuthenticationProvider -> supportedAuthenticationProvider.supports(authentication.getClass()))
                .map(supportedAuthenticationProvider -> supportedAuthenticationProvider.authenticate(authentication))
                .findFirst().orElseGet(() -> Mono.error(new AuthenticationException("Authentication failed!!")));
    }
}
