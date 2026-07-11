package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestToEntityMapper {

    public AccessEntity buildAccessorEntity(AccessorRequest accessorRequest) {
        return AccessEntity.builder()
                .username(accessorRequest.getUsername())
                .password(accessorRequest.getPassword())
                .firstName(accessorRequest.getFirstName())
                .middleName(accessorRequest.getMiddleName())
                .lastName(accessorRequest.getLastName())
                .accessEnabled(true)
                .accessExpired(false)
                .credentialsExpired(false)
                .accessLocked(false)
                .build();
    }

}
