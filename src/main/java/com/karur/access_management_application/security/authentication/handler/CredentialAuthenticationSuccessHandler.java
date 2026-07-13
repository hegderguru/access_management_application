package com.karur.access_management_application.security.authentication.handler;

import com.karur.access_management_application.security.authentication.provider.JwtTokenProvider;
import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.util.CommonUtil;
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

import java.util.HashMap;
import java.util.Map;

@Service
public class CredentialAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private EntityToReadMapper entityToReadMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        return entityToReadMapper.buildAccessDetail(authentication.getName())
                .map(accessDetail -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("username", accessDetail.getUsername());
                    claims.put("firstName", accessDetail.getFirstName());
                    claims.put("middleName", accessDetail.getMiddleName());
                    claims.put("lastName", accessDetail.getLastName());

                    String authorities = String.join(":", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).toList());
                    claims.put("authorities", authorities);

                    return jwtTokenProvider.generateToken(accessDetail.getUsername(), claims);
                })
                .flatMap(token -> {
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    // Map the token to a JSON key-value pair structure
                    Map<String, String> responseBody = Map.of("token", token);

                    // Safely wrap the checked exception thrown by ObjectMapper inside a Mono
                    return Mono.fromCallable(() -> CommonUtil.writeValueAsBytes(responseBody))
                            .flatMap(bytes -> response.writeWith(Mono.just(response.bufferFactory().wrap(bytes))));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }));
    }
}
