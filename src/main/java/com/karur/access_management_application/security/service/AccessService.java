package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.*;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.validate.annotation.ValidateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AccessService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @ValidateData
    public Mono<AccessDetail> fetchAccessDetails(String username) {
        return accessRepository.fetchAccessEntity(username).flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
    }

    public Mono<AccessDetail> create(AccessRequest accessRequest) {
        return Mono.defer(() -> Mono.just(requestToEntityMapper.buildAccessEntity(accessRequest)))
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                .map(savedAccessEntity -> entityToReadMapper.buildAccessDetail(savedAccessEntity));
    }

    public Mono<AccessDetail> update(AccessRequest accessRequest) {
        return requestToEntityMapper.saveOrUpdateAccess(accessRequest)
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                .map(savedAccessEntity -> entityToReadMapper.buildAccessDetail(savedAccessEntity));
    }


}
