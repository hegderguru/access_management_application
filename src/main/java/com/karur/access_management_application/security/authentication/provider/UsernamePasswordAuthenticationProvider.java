package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.service.AccessDetailsPasswordService;
import com.karur.access_management_application.security.service.AccessDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.Objects;

@Service
public class UsernamePasswordAuthenticationProvider implements SupportedAuthenticationProvider {

    @Autowired
    AccessDetailsService accessDetailsService;

    @Autowired
    AccessDetailsPasswordService accessDetailsPasswordService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> authenticatinClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authenticatinClass);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            return Mono.error(new IllegalAccessException("Username and password authentication not supported"));
        }
        if (Objects.isNull(authentication.getName()) || Objects.isNull(authentication.getCredentials())) {
            return Mono.error(new BadCredentialsException("Invalid username or password"));
        }
        String password = (String) authentication.getCredentials();
        return accessDetailsService.findByUsername(authentication.getName())
                .switchIfEmpty(Mono.error(new IllegalAccessException("User not found")))
                .flatMap(userDetails -> {
                    if (!userDetails.isAccountNonExpired() || !userDetails.isAccountNonLocked() || !userDetails.isCredentialsNonExpired() || !userDetails.isEnabled()) {
                        return Mono.error(new AuthenticationException("This account is forbiddon the access"));
                    }
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Invalid Credentials"));
                    }
                    if (passwordEncoder.upgradeEncoding(userDetails.getPassword())) {
                        return accessDetailsPasswordService.updatePassword(userDetails, passwordEncoder.encode(password));
                    }
                    return Mono.just(userDetails);
                })
                .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
    }
}
