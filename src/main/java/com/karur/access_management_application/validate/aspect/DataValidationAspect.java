package com.karur.access_management_application.validate.aspect;

import com.karur.access_management_application.validate.annotation.ValidateData;
import com.karur.access_management_application.validate.service.ValidateDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
public class DataValidationAspect {

    @Autowired
    private ValidateDataProcessor validateDataProcessor;

    @Around("@annotation(validateData)")
    public Object pursueValidation(ProceedingJoinPoint joinPoint, ValidateData validateData) throws Throwable {

        // 1. Execute the actual repository/service method to get the original Mono pipeline
        Mono<?> originalResultMono = (Mono<?>) joinPoint.proceed();

        // 2. Extract Authentication safely from the Reactive Context
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                // Fallback if the user is unauthenticated or context isn't propagated
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("ReactiveSecurityContext is EMPTY. Proceeding WITHOUT validation.");
                    return Mono.empty();
                }))
                // Use flatMap to evaluate both pipelines concurrently
                .flatMap(authentication ->
                        originalResultMono.flatMap(payload ->
                                validateDataProcessor.validate(payload, authentication)
                                        .then(Mono.just(payload))
                        )
                )
                // CRITICAL: If context was empty, make sure we still return the original database data!
                .switchIfEmpty((Mono) originalResultMono);
    }
}