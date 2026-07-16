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
public class ValidateDataAspect {

    @Autowired
    private ValidateDataProcessor validateDataProcessor;

    @Around("@annotation(validateData)")
    public Object pursueValidation(ProceedingJoinPoint joinPoint, ValidateData validateData) throws Throwable {
        Mono<?> originalResultMono = (Mono<?>) joinPoint.proceed();
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("ReactiveSecurityContext is EMPTY. Proceeding WITHOUT validation.");
                    return Mono.empty();
                }))
                .flatMap(authentication ->
                        originalResultMono.flatMap(payload ->validateDataProcessor.validate(payload, authentication)
                                        .then(Mono.just(payload))
                        )
                )
                .switchIfEmpty((Mono) originalResultMono);
    }
}