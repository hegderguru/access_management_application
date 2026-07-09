package com.karur.access_management_application.security.authentication.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
        return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
    }
}
