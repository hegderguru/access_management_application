package com.karur.access_management_application.verifyAuthority.aspect;

import com.karur.access_management_application.verifyAuthority.service.SecurityAnnotationProcessor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    //@Around("@annotation(VerifyAuthority)")
    @Around("@annotation(com.karur.access_management_application.verifyAuthority.annotation.VerifyAuthority)")
    public void afterSetter(JoinPoint joinPoint) {
        // 2. Grabs the bean instance that just had its setter invoked
        Object targetObject = joinPoint.getTarget();

        // 3. Runs your custom annotation processor over the fields
        SecurityAnnotationProcessor.processVerification(targetObject);
    }
}
