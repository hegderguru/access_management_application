package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
import com.karur.access_management_application.security.service.AccessDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Service
@Profile({"local", "test"})
public class TestAccessAuthenticationProvider implements SupportedAuthenticationProvider {

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> authenticatinClass) {
        return TestingAuthenticationToken.class.isAssignableFrom(authenticatinClass);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            Mono.error(new AuthenticationException("Access is not allowed"));
        }
        return entityToReadMapper.buildAccessDetail(authentication.getName())
                .switchIfEmpty(Mono.error(new IllegalAccessException("User not found")))
                .flatMap(Mono::just)
                .map(userDetails -> new TestingAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
    }
}
