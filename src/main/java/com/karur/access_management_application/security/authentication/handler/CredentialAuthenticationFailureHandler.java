package com.karur.access_management_application.security.authentication.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
public class CredentialAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return webFilterExchange.getExchange().getResponse()
                .writeWith(Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse().bufferFactory()
                        .wrap(new ObjectMapper().writeValueAsBytes(exception.getMessage())))));
    }
}
