package com.karur.access_management_application.security;

import com.karur.access_management_application.security.authentication.converter.CredentialAuthenticationConverter;
import com.karur.access_management_application.security.authentication.converter.JwtAuthenticationConverter;
import com.karur.access_management_application.security.authentication.handler.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity, AuthenticationWebFilter credentialAuthenticationWebFilter
            , AuthenticationWebFilter jwtAuthenticationWebFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        return serverHttpSecurity
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(authenticationEntryPoint))
                .headers(headerSpec -> {
                    headerSpec.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                            .cache(ServerHttpSecurity.HeaderSpec.CacheSpec::disable);
                })
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers("/token").permitAll()
                        .anyExchange().hasAnyAuthority("creds"))
                .addFilterAt(credentialAuthenticationWebFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter credentialAuthenticationWebFilter(@Qualifier("customAuthenticationManager") ReactiveAuthenticationManager authenticationManager, CredentialAuthenticationConverter credentialAuthenticationConverter
            , CredentialAuthenticationSuccessHandler credentialAuthenticationSuccessHandler, CredentialAuthenticationFailureHandler credentialAuthenticationFailureHandler) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/token"));
        filter.setServerAuthenticationConverter(credentialAuthenticationConverter);
        filter.setAuthenticationSuccessHandler(credentialAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(credentialAuthenticationFailureHandler);
        return filter;
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(@Qualifier("customAuthenticationManager") ReactiveAuthenticationManager authenticationManager, JwtAuthenticationConverter jwtAuthenticationConverter
            , JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler, JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setRequiresAuthenticationMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/token")));
        filter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        filter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
