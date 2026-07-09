package com.karur.access_management_application.security.authentication.provider;

import org.springframework.security.authentication.ReactiveAuthenticationManager;

public interface SupportedAuthenticationProvider extends ReactiveAuthenticationManager {

    boolean supports(Class<?> authenticatinClass);
}
