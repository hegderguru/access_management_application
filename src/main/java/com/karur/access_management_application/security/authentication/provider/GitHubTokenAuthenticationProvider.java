package com.karur.access_management_application.security.authentication.provider;

import com.karur.access_management_application.security.authentication.token.GitHubTokenAuthenticationToken;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
public class GitHubTokenAuthenticationProvider implements SupportedAuthenticationProvider {

    private WebClient webClient;

    private EntityToReadMapper entityToReadMapper;

    public GitHubTokenAuthenticationProvider(@Value("${authentication.github.api.url}") String gitHubTokenUrl, EntityToReadMapper entityToReadMapper) {
        webClient = WebClient.builder()
                .baseUrl(gitHubTokenUrl)
                .build();
        this.entityToReadMapper = entityToReadMapper;
    }

    @Override
    public boolean supports(Class<?> authenticatinClass) {
        return GitHubTokenAuthenticationToken.class.isAssignableFrom(authenticatinClass);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            return Mono.error(new AuthenticationException("Unsupported authentication feature"));
        }
        GitHubTokenAuthenticationToken gitHubTokenAuthenticationToken = (GitHubTokenAuthenticationToken) authentication;
        if (Objects.isNull(authentication.getPrincipal()) || Objects.isNull(authentication.getCredentials())) {
            return Mono.error(new BadCredentialsException("Invalid Credentials"));
        }

        return webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((authentication.getPrincipal() + ":" + authentication.getCredentials()).getBytes(StandardCharsets.UTF_8)))
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new AuthenticationException("Authentication failed due to %s".formatted(body)))))
                .bodyToMono(Map.class)
                .flatMap(userProfile -> {
                    String login = (String) userProfile.get("login");
                    String name = (String) userProfile.get("name");
                    String email = (String) userProfile.get("email");
                    if (!login.equalsIgnoreCase(authentication.getPrincipal().toString())) {
                        return Mono.error(new AuthenticationException("Invalid credentials"));
                    }
                    return entityToReadMapper.buildAccessDetail(authentication.getName())
                            .switchIfEmpty(Mono.error(new IllegalAccessException("User not found")))
                            .flatMap(Mono::just)
                            .map(userDetails -> new GitHubTokenAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorityDetails()));
                });
    }
}
