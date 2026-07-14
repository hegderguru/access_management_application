package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.mapper.entiyToRequest.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.*;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
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

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AuthorityRequestToEntityMapper authorityRequestToEntityMapper;

    @Autowired
    RoleRequestToEntityMapper roleRequestToEntityMapper;

    @Autowired
    PermissionRequestToEntityMapper permissionRequestToEntityMapper;

    public Mono<AccessDetail> fetchAccessDetails(String username) {
        return accessRepository.fetchAccessEntity(username).flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
    }

    public Mono<AccessDetail> update(AccessRequest accessRequest) {
        return requestToEntityMapper.saveOrUpdateAccess(accessRequest)
                // 1. flatMap allows you to wait for the asynchronous DB save to complete
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                // 2. map synchronously transforms the saved result into your read DTO
                .map(savedAccessEntity -> entityToReadMapper.buildAccessDetail(savedAccessEntity));
    }


}
