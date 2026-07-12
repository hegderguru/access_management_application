package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.RequestToEntityMapper;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessDetailsUpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AccessService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Autowired
    EntityToAccessReuestMapper entityToAccessReuestMapper;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    public Mono<AccessDetail> saveAccess(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .switchIfEmpty(Mono.just(requestToEntityMapper.buildAccessEntity(accessRequest)))
                .flatMap(accessEntity -> {
                    Mono.just(AccessDetailsUpdateUtil.accessChanges(entityToAccessReuestMapper.buildAccessRequest(accessEntity), accessRequest))
                            .flatMap(changes -> newAuthorities(accessEntity, changes)
                                    .map(unused -> updateAuthorities(accessEntity, changes)));
                    return entityToReadMapper.buildAccessDetail(accessEntity.getUsername());
                });
    }

    private Mono<Void> newAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewAuthorityRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildAuthorityEntity((AuthorityRequest) change.getRightValue())))
                .flatMap(authorityEntity -> {
                    accessEntity.getAuthorityEntities().add(authorityEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getUpdateAuthorityRequest(changes))
                .flatMap(change -> {
                    AuthorityRequest authorityRequest = (AuthorityRequest) change.getRight();
                    AuthorityEntity authorityEntity = accessEntity.getAuthorityEntities().stream().filter(authorityEntity1 -> authorityEntity1.getName().equalsIgnoreCase(authorityRequest.getName())).findFirst().get();
                    switch (AuthorityRequest.Fields.valueOf(change.getField().getName())) {
                        case description -> authorityEntity.setName(ChangeUtil.getStringElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
