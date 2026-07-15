package com.karur.access_management_application.validate.service;

import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.PermissionDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import com.karur.access_management_application.security.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Service
public class ValidateDataProcessor {

    @Autowired
    AccessService accessService;

    public Mono<Void> validate(Object payload, Authentication authentication) {
        // 1. Instantly check if the payload matches the expected class type
        if (payload instanceof AccessDetail accessDetail) { // Using Java 16+ Pattern Matching

            log.info("ValidateDataProcessor :: Payload received successfully: {}", accessDetail);

            // 2. Process the data already handed to us by the Aspect (No database call!)
            return Mono.just(accessDetail)
                    .flatMapIterable(AccessDetail::getAuthorities)
                    .flatMapIterable(AuthorityDetail::getRoleDetails)
                    .flatMapIterable(RoleDetail::getPermissionDetails)
                    // 1. Gather all individual permissions passing through the stream into a List
                    .collectList()
                    // 2. Pass the complete list into the validation method once gathered
                    .flatMap(allPermissions -> {
                        log.info("Gathered {} total permissions. Invoking deep validation path.", allPermissions.size());
                        return validateDeepPermission(payload, allPermissions);
                    })
                    .then();

        }

        log.warn("ValidateDataProcessor :: Payload is not an instance of AccessDetail. Skipping validation.");
        return Mono.empty();
    }

    private Mono<Void> validateDeepPermission(Object payload, List<PermissionDetail> permissionDetails) {
        for (Field field : payload.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                /*log.info("Field Name: '{}' | Field Value: '{}' ",
                        field.getName(), field.get(payload));*/
                if (permissionDetails.stream().anyMatch(permissionDetail -> permissionDetail.fullyQualifiedFieldPath().equalsIgnoreCase(field.getDeclaringClass().getName() + "." + field.getName()))) {
                    field.set(payload, null);
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to access field values on payload via reflection: {}", field.getName(), e);
            }
        }

        return Mono.empty();
    }


}
