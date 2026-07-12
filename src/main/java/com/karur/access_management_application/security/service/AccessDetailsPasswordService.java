package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
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

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, @Nullable String newPassword) {
        return accessEntityRepository.findByUsername(userDetails.getUsername())
                .flatMap(accessEntity1 -> {
                    accessEntity1.setPassword(newPassword);
                    return accessEntityRepository.save(accessEntity1)
                            .flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
                });
    }
}
