package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.model.request.AccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AccessRequestToEntityMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    public AccessEntity buildAccessEntity(AccessRequest accessRequest) {
        return AccessEntity.builder()
                .username(accessRequest.getUsername())
                .password(passwordEncoder.encode(accessRequest.getPassword()))
                .firstName(accessRequest.getFirstName())
                .middleName(accessRequest.getMiddleName())
                .lastName(accessRequest.getLastName())
                .accessEnabled(true)
                .accessExpired(false)
                .credentialsExpired(false)
                .accessLocked(false)
                .authorityEntities(new ArrayList<>())
                .build();
    }

}
