package com.karur.access_management_application.validate.service;

import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.PermissionDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ValidateDataProcessor {

    @Autowired
    AccessService accessService;

    public Mono<Void> validate(Object payload, Authentication authentication) {
        if (Objects.isNull(payload)) {
            return Mono.empty();
        }
        if (payload instanceof AccessDetail accessDetail) { // Using Java 16+ Pattern Matching
            log.info("ValidateDataProcessor :: Payload received successfully: {}", accessDetail);
            return Mono.just(accessDetail)
                    .flatMapIterable(AccessDetail::getAuthorityDetails)
                    .flatMapIterable(AuthorityDetail::getRoleDetails)
                    .flatMapIterable(RoleDetail::getPermissionDetails)
                    .collectList()
                    .flatMap(allPermissions -> {
                        log.info("Gathered {} total permissions. Invoking deep validation path.", allPermissions.size());
                        return validatePermissionOnPermissionDetail(payload, allPermissions);
                    })
                    .then();

        }
        if (payload instanceof AccessRequest accessRequest) { // Using Java 16+ Pattern Matching
            log.info("ValidateDataProcessor :: Payload received successfully: {}", accessRequest);
            return Mono.just(accessRequest)
                    .flatMapIterable(AccessRequest::getAuthorityRequests)
                    .flatMapIterable(AuthorityRequest::getRoleRequests)
                    .flatMapIterable(RoleRequest::getPermissionRequests)
                    .collectList()
                    .flatMap(allPermissions -> {
                        log.info("Gathered {} total permissions. Invoking deep validation path.", allPermissions.size());
                        return validatePermissionOnPermissionRequest(payload, allPermissions);
                    })
                    .then();

        }
        log.warn("ValidateDataProcessor :: Payload is not an instance of AccessDetail. Skipping validation.");
        return Mono.empty();
    }

    private Mono<Void> validatePermissionOnPermissionDetail(Object payload, List<PermissionDetail> permissionDetails) {
        for (Field field : payload.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (permissionDetails.stream().anyMatch(permissionDetail -> permissionDetail.getFullyQualifiedFieldName().equalsIgnoreCase(field.getDeclaringClass().getName() + "." + field.getName())
                && (permissionDetail.getRead()))) {
                    continue;
                }
                field.set(payload, null);
            } catch (IllegalAccessException e) {
                log.error("Failed to access field values on payload via reflection: {}", field.getName(), e);
            }
        }

        return Mono.empty();
    }

    private Mono<Void> validatePermissionOnPermissionRequest(Object payload, List<PermissionRequest> permissionDetails) {
        for (Field field : payload.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (permissionDetails.stream().anyMatch(permissionRequest -> permissionRequest.getFullyQualifiedFieldName().equalsIgnoreCase(field.getDeclaringClass().getName() + "." + field.getName())
                && (permissionRequest.getCreate() || permissionRequest.getUpdate()))) {
                    continue;
                }
                field.set(payload, null);
            } catch (IllegalAccessException e) {
                log.error("Failed to access field values on payload via reflection: {}", field.getName(), e);
            }
        }

        return Mono.empty();
    }


}
