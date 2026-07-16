package com.karur.access_management_application.validate.aspect;

import com.karur.access_management_application.security.model.read.AccessDetail;
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
        Object result = joinPoint.proceed();

        if (result instanceof Mono<?>) {
            Mono<?> originalResultMono = (Mono<?>) result;
            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("ReactiveSecurityContext is EMPTY. Proceeding WITHOUT validation.");
                        return Mono.empty();
                    }))
                    .flatMap(authentication -> originalResultMono.flatMap(payload -> {
                                if (payload instanceof AccessDetail) {
                                    // 1. Added return statement for the validation path
                                    return validateDataProcessor.validate(payload, authentication)
                                            .then(Mono.just(payload));
                                } else {
                                    // 2. Added return statement to safely pass unvalidated types through
                                    log.debug("Payload is not an AccessDetail. Passing through without validation.");
                                    return Mono.just(payload);
                                }
                            })
                    );
        }

        return Mono.error(new IllegalArgumentException("The validated method must return a Mono stream. Found: "
                + (result != null ? result.getClass().getName() : "null")));
    }



}