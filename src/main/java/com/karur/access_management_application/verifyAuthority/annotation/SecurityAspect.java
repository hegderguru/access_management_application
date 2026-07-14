package com.karur.access_management_application.verifyAuthority.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @After("execution(* com.example.model..set*(..))")
    public void afterSetter(JoinPoint joinPoint) {
        Object targetObject = joinPoint.getTarget();
        SecurityAnnotationProcessor.processVerification(targetObject);
    }
}
