package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.*;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessRequestUtil;
import lombok.extern.slf4j.Slf4j;
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
    EntityToReadMapper entityToReadMapper;

    public Mono<Void> createPermissions(String appId,List<Boolean[]> permissions) {
        return accessRepository.createPermissions(appId,permissions, List.of(AccessDetail.builder().build(), AccessRequest.builder().build()));
    }

    /*Read Starts*/
   
    public Mono<AccessDetail> fetchAccessDetails(String username) {
        return accessRepository.fetchAccessEntity(username)
                .flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
    }

   
    public Mono<AuthorityDetail> fetchAuthorityDetail(String name) {
        return accessRepository.fetchAuthorityEntity(name)
                .doOnNext(authorityEntity -> log.info("Auth {}", authorityEntity))
                .map(authorityEntity -> entityToReadMapper.buildAuthorityDetail(authorityEntity));
    }

   
    public Mono<RoleDetail> fetchRoleDetail(String name) {
        return accessRepository.fetchRoleEntity(name)
                .flatMap(roleEntity -> Mono.just(entityToReadMapper.buildRoleDetail(roleEntity)));
    }
    /*Read Ends*/

    /*Create Starts*/
   
    public Mono<AccessDetail> createAccess(AccessRequest accessRequest) {
        return Mono.defer(() -> Mono.just(requestToEntityMapper.buildOnlyAccessEntity(accessRequest)))
                .flatMap(accessEntity -> Flux.fromIterable(accessRequest.getAuthorityRequests())
                        .flatMap(authorityRequest -> accessRepository.createAccessAuthorityEntity(accessEntity.getId(), authorityRequest.getName()))
                        .then(Mono.just(accessEntity)))
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                .map(savedAccessEntity -> entityToReadMapper.buildAccessDetail(savedAccessEntity));
    }

   
    public Mono<AuthorityDetail> createAuthority(AuthorityRequest authorityRequest) {
        return Mono.defer(() -> Mono.just(requestToEntityMapper.buildOnlyAuthorityEntity(authorityRequest)))
                .flatMap(authorityEntity -> Flux.fromIterable(authorityRequest.getRoleRequests())
                        .flatMap(roleRequest -> accessRepository.createAuthorityRoleEntity(authorityEntity.getId(), roleRequest.getName()))
                        .then(Mono.just(authorityEntity)))
                .flatMap(authorityEntity -> accessRepository.saveAuthorityEntity(authorityEntity))
                .map(authorityEntity -> entityToReadMapper.buildAuthorityDetail(authorityEntity));
    }


    public Mono<RoleDetail> createRole(RoleRequest roleRequest) {
        return Mono.defer(() -> Mono.just(requestToEntityMapper.buildOnlyRoleEntity(roleRequest)))
                .flatMap(roleEntity -> accessRepository.saveRoleEntity(roleEntity))
                .flatMap(roleEntity -> Flux.fromIterable(roleRequest.getPermissionRequests())
                        .flatMap(permissionRequest -> accessRepository.createRolePermissionEntity(roleEntity.getId(), permissionRequest))
                        .then(Mono.just(roleEntity)))
                .map(roleEntity -> entityToReadMapper.buildRoleDetail(roleEntity));
    }
    /*Create ends*/

    /*Update starts*/
   
    public Mono<AccessDetail> updateAccess(AccessRequest accessRequest) {
        return Mono.defer(() -> requestToEntityMapper.updateAccess(AccessRequestUtil.buildAccessRequest(accessRequest)))
                .flatMap(accessEntity -> Flux.fromIterable(accessRequest.getAuthorityRequests())
                        .flatMap(authorityRequest -> accessRepository.updateAccessAuthorityEntity(accessEntity.getId(), authorityRequest.getName()))
                        .then(Mono.just(accessEntity)))
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                .map(savedAccessEntity -> entityToReadMapper.buildAccessDetail(savedAccessEntity));
    }

   
    public Mono<AuthorityDetail> updateAuthority(AuthorityRequest authorityRequest) {
        return Mono.defer(() -> requestToEntityMapper.updateAuthority(authorityRequest))
                .flatMap(authorityEntity -> Flux.fromIterable(authorityRequest.getRoleRequests())
                        .flatMap(roleRequest -> accessRepository.updateAuthorityRoleEntity(authorityEntity.getId(), roleRequest.getName()))
                        .then(Mono.just(authorityEntity)))
                .flatMap(authorityEntity -> accessRepository.saveAuthorityEntity(authorityEntity))
                .map(authorityEntity -> entityToReadMapper.buildAuthorityDetail(authorityEntity));
    }

   
    public Mono<RoleDetail> updateRole(RoleRequest roleRequest) {
        return Mono.defer(() -> requestToEntityMapper.updateRole(roleRequest))
                .flatMap(roleEntity -> Flux.fromIterable(roleRequest.getPermissionRequests())
                        .flatMap(permissionRequest -> accessRepository.updateRolePermissionEntity(roleEntity.getId(), permissionRequest))
                        .then(Mono.just(roleEntity)))
                .flatMap(roleEntity -> accessRepository.saveRoleEntity(roleEntity))
                .map(roleEntity -> entityToReadMapper.buildRoleDetail(roleEntity));
    }
    /*Update ends*/
}
