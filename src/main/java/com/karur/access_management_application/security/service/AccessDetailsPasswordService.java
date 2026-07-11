package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.repository.AccessEntityRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessDetailsPasswordService implements ReactiveUserDetailsPasswordService {

    @Autowired
    AccessEntityRepository accessEntityRepository;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, @Nullable String newPassword) {
        AccessEntity accessEntity = (AccessEntity) userDetails;
        accessEntity.setPassword(newPassword);
        return accessEntityRepository.save(accessEntity).flatMap(accessorEntity1 -> Mono.just((UserDetails) accessorEntity1));

    }
}
