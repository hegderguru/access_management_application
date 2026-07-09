package com.karur.access_management_application.security.authentication.converter;

import com.karur.access_management_application.security.authentication.token.GitHubTokenAuthenticationToken;
import lombok.Data;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import javax.naming.AuthenticationException;

@Service
public class CredentialAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        return bytes;
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                })
                .map(bytes -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(bytes, AuthRequest.class);
                })
                .map(authRequest -> {
                    Authentication authentication = null;
                    if ("github".equalsIgnoreCase(authRequest.getProvider())) {
                        authentication = new GitHubTokenAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
                    } else if ("test".equalsIgnoreCase(authRequest.getProvider())) {
                        authentication = new TestingAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
                    } else {
                        authentication = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
                    }
                    return authentication;
                })
                .onErrorMap(throwable -> new AuthenticationException(throwable.getMessage()));
    }

    @Data
    private static class AuthRequest {
        private String username;
        private String password;
        private String provider;
    }
}
