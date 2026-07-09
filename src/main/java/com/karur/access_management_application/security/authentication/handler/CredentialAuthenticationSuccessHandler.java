package com.karur.access_management_application.security.authentication.handler;

import com.karur.access_management_application.security.authentication.jwt.JwtTokenProvider;
import com.karur.access_management_application.security.repository.AccessorEntityRepository;
import com.karur.access_management_application.security.repository.AccessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class CredentialAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private AccessorRepository accessorRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        return accessorRepository.findAccessorEntityByUsername(authentication.getName())
                .map(accessorEntity -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("username", accessorEntity.getUsername());
                    claims.put("firstName", accessorEntity.getFirstName());
                    claims.put("middleName", accessorEntity.getMiddleName());
                    claims.put("lastName", accessorEntity.getLastName());

                    String authorities = String.join(":", accessorEntity.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).toList());
                    claims.put("authorities", authorities);

                    return jwtTokenProvider.generateToken(accessorEntity.getUsername(), claims);
                })
                .flatMap(token -> {
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    // Map the token to a JSON key-value pair structure
                    Map<String, String> responseBody = Map.of("token", token);

                    // Safely wrap the checked exception thrown by ObjectMapper inside a Mono
                    return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(responseBody))
                            .flatMap(bytes -> response.writeWith(Mono.just(response.bufferFactory().wrap(bytes))));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }));
    }
}
