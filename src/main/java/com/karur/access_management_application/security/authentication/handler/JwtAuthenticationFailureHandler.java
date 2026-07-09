package com.karur.access_management_application.security.authentication.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
public class JwtAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        webFilterExchange.getExchange().getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return webFilterExchange.getExchange().getResponse()
                .writeWith(Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse().bufferFactory()
                        .wrap(new ObjectMapper().writeValueAsBytes(exception.getMessage())))));
    }
}
