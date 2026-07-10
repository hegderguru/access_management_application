package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.model.AccessorEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessRequestToEntityMapper {

    public AccessorEntity buildAccessorEntity(AccessorRequest accessorRequest){
        return Mono.just(AccessorEntity.builder()
                .username(accessorRequest.getUsername())
                .password(accessorRequest.getPassword())
                .firstName(accessorRequest.getFirstName())
                .middleName(accessorRequest.getMiddleName())
                .lastName(accessorRequest.getLastName())
                .accessEnabled(true)
                .accessExpired(false)
                .credentialsExpired(false)
                .accessLocked(false)
                .build());
    }

}
