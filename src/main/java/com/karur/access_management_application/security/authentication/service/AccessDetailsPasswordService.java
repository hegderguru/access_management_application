package com.karur.access_management_application.security.authentication.service;

import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.repository.inter.table.AccessEntityRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessDetailsPasswordService implements ReactiveUserDetailsPasswordService {

    @Autowired
    AccessEntityRepository accessEntityRepository;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, @Nullable String newPassword) {
        return accessEntityRepository.findByUsername(userDetails.getUsername())
                .flatMap(accessEntity1 -> {
                    accessEntity1.setPassword(newPassword);
                    return accessEntityRepository.save(accessEntity1)
                            .flatMap(accessEntity -> entityToReadMapper.buildUserDetail(accessEntity.getUsername()));
                });
    }
}
