package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.mapper.requestToEntity.AccessRequestToEntityMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
@Repository
public class AccessRepository {

    @Autowired
    AccessEntityRepository accessEntityRepository;

    @Autowired
    AuthorityEntityRepository authorityEntityRepository;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    PermissionEntityRepository permissionEntityRepository;

    @Autowired
    RolePermissionIdRepository rolePermissionIdRepository;

    @Autowired
    AuthorityRoleIdRepository authorityRoleIdRepository;

    @Autowired
    AccessAuthorityIdRepository accessAuthorityIdRepository;

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    public Mono<AccessEntity> fetchOnlyAccessEntity(String username) {
        return accessEntityRepository.findByUsername(username)
                .flatMap(accessEntity -> fetchAllAccessAuthorityEntities(accessEntity)
                        .collectList()
                        .flatMap(accessAuthorityEntities -> fetchOnlyAuthorityEntities(accessAuthorityEntities)
                                .collectList()
                                .doOnNext(accessEntity::setAuthorityEntities)
                                .then(Mono.just(accessEntity))));
    }

    public Flux<AuthorityEntity> fetchOnlyAuthorityEntities(List<AccessAuthorityEntity> accessAuthorityEntities) {
        return authorityEntityRepository.findByIdIn(accessAuthorityEntities.stream().map(AccessAuthorityEntity::authorityId).toList());
    }

    public Mono<AccessEntity> findAccessEntityByUsername(String username) {
        return accessEntityRepository.findByUsername(username);
    }

    public Mono<AccessEntity> fetchAccessEntity(String username) {
        return accessEntityRepository.findByUsername(username)
                .flatMap(accessEntity -> fetchAllAccessAuthorityEntities(accessEntity)
                        .collectList()
                        .flatMap(accessAuthorityEntities -> fetchAllAuthorityEntities(accessAuthorityEntities)
                                .collectList()
                                .doOnNext(accessEntity::setAuthorityEntities)
                                .then(Mono.just(accessEntity))));
    }

    public Flux<AuthorityEntity> fetchAuthorityEntities(List<Long> ids) {
        return Flux.fromIterable(ids).flatMap(this::fetchAuthorityEntity);
    }

    public Flux<AuthorityEntity> fetchAllAuthorityEntities(List<AccessAuthorityEntity> accessAuthorityEntities) {
        List<Long> ids = accessAuthorityEntities.stream().map(AccessAuthorityEntity::authorityId).toList();
        return fetchAuthorityEntities(ids);
    }

    public Flux<AccessAuthorityEntity> fetchAllAccessAuthorityEntities(AccessEntity accessEntity) {
        return accessAuthorityIdRepository.findByAccessId(accessEntity.getId());
    }

    public Mono<AuthorityEntity> fetchAuthorityEntity(Long id) {
        return authorityEntityRepository.findById(id)
                .flatMap(authorityEntity -> fetchAllAuthorityRoleEntities(authorityEntity)
                        .collectList()
                        .flatMap(authorityRoleEntities -> fetchAllRoleEntities(authorityRoleEntities)
                                .collectList()
                                .doOnNext(authorityEntity::setRoleEntities)
                                .then(Mono.just(authorityEntity))));
    }

    public Flux<AuthorityRoleEntity> fetchAllAuthorityRoleEntities(AuthorityEntity authorityEntity) {
        return authorityRoleIdRepository.findByAuthorityId(authorityEntity.getId());
    }

    public Flux<RoleEntity> fetchAllRoleEntities(List<AuthorityRoleEntity> authorityRoleEntities) {
        List<Long> ids = authorityRoleEntities.stream().map(AuthorityRoleEntity::roleId).toList();
        return fetchRoleEntities(ids);
    }

    public Flux<RoleEntity> fetchRoleEntities(List<Long> ids) {
        return Flux.fromIterable(ids).flatMap(this::fetchRoleEntity);
    }

    public Mono<RoleEntity> fetchRoleEntity(Long id) {
        return roleEntityRepository.findById(id)
                .flatMap(roleEntity -> fetchAllRolePermissionEntities(roleEntity)
                        .collectList()
                        .flatMap(rolePermissionEntities -> fetchAllPermissionEntities(rolePermissionEntities)
                                .collectList()
                                .doOnNext(roleEntity::setPermissionEntities)
                                .then(Mono.just(roleEntity)))
                );
    }

    public Flux<RolePermissionEntity> fetchAllRolePermissionEntities(RoleEntity roleEntity) {
        return rolePermissionIdRepository.findByRoleId(roleEntity.getId());
    }

    public Flux<PermissionEntity> fetchAllPermissionEntities(List<RolePermissionEntity> rolePermissionEntities) {
        List<Long> ids = rolePermissionEntities.stream().map(RolePermissionEntity::permissionId).toList();
        return fetchPermissionEntities(ids);
    }

    public Flux<PermissionEntity> fetchPermissionEntities(List<Long> ids) {
        return permissionEntityRepository.findByIdIn(ids);
    }

    public Mono<AccessEntity> saveAccessEntity(AccessEntity accessEntity) {
        return accessEntityRepository.save(accessEntity)
                .flatMap(accessEntity1 -> Flux.fromIterable(accessEntity1.getAuthorityEntities()).flatMap(this::saveAuthorityEntity)
                        .flatMap(authorityEntity -> accessAuthorityIdRepository.findByAccessIdAndAuthorityId(accessEntity1.getId(), authorityEntity.getId())
                                .switchIfEmpty(Flux.just(accessRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity1.getId(), authorityEntity)))
                                .flatMap(accessAuthorityEntity -> accessAuthorityIdRepository.save(accessAuthorityEntity)))
                        .then(Mono.just(accessEntity1)));
    }

    public Mono<AuthorityEntity> saveAuthorityEntity(AuthorityEntity authorityEntity) {
        return authorityEntityRepository.save(authorityEntity)
                .flatMap(authorityEntity1 -> Flux.fromIterable(authorityEntity1.getRoleEntities()).flatMap(this::saveRoleEntity)
                        .flatMap(roleEntity -> authorityRoleIdRepository.findByAuthorityIdAndRoleId(authorityEntity1.getId(), roleEntity.getId())
                                .switchIfEmpty(Flux.just(accessRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity1.getId(), roleEntity)))
                                .flatMap(rolePermissionEntity -> authorityRoleIdRepository.save(rolePermissionEntity)))
                        .then(Mono.just(authorityEntity1)));
    }

    public Mono<RoleEntity> saveRoleEntity(RoleEntity roleEntity) {
        return roleEntityRepository.save(roleEntity)
                .flatMap(roleEntity1 -> Flux.fromIterable(roleEntity1.getPermissionEntities()).flatMap(this::savePermissionEntity)
                        .flatMap(permissionEntity -> rolePermissionIdRepository.findByRoleIdAndPermissionId(roleEntity1.getId(), permissionEntity.getId())
                                .switchIfEmpty(Flux.just(accessRequestToEntityMapper.buildRolePermissionEntity(roleEntity1.getId(), permissionEntity)))
                                .flatMap(rolePermissionEntity -> rolePermissionIdRepository.save(rolePermissionEntity)))
                        .then(Mono.just(roleEntity1)));
    }

    public Mono<PermissionEntity> savePermissionEntity(PermissionEntity permissionEntity) {
        return permissionEntityRepository.save(permissionEntity);
    }
}
