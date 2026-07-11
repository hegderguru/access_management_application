package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.authentication.model.AccessEntity;
import com.karur.access_management_application.security.repository.AccessorEntityRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class AccessDetailsPasswordService implements ReactiveUserDetailsPasswordService {

    @Autowired
    AccessorEntityRepository accessorEntityRepository;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, @Nullable String newPassword) {
        AccessEntity accessEntity = (AccessEntity) userDetails;
        accessEntity.setPassword(newPassword);
        return accessorEntityRepository.save(accessEntity).flatMap(accessorEntity1 -> Mono.just((UserDetails) accessorEntity1));

    }
}
