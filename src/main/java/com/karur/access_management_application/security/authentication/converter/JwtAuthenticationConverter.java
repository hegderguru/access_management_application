package com.karur.access_management_application.security.authentication.converter;

import com.karur.access_management_application.security.authentication.provider.JwtTokenProvider;
import com.karur.access_management_application.security.authentication.entity.AuthorityEntity;
import com.karur.access_management_application.security.authentication.token.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(bearerToken)) {
            String token = jwtTokenProvider.extractToken(bearerToken);
            List<AuthorityEntity> grantedAuthorityEntities = jwtTokenProvider.getAuthorities(token).stream().map(authority -> AuthorityEntity.builder().name(authority).build()).toList();
            return Mono.just(new JwtAuthenticationToken(jwtTokenProvider.getUsernameFromToken(token), token,grantedAuthorityEntities));
        }
        return Mono.empty();
    }
}
