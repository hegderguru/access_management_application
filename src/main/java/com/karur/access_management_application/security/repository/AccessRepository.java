package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.AccessRequestToEntityMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.AuthorityRequestToEntityMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.PermissionRequestToEntityMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.RoleRequestToEntityMapper;
import com.karur.access_management_application.security.repository.inter.table.AccessEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.AuthorityEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.PermissionEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.RoleEntityRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.AccessAuthorityIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.AuthorityRoleIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.RolePermissionIdRepository;
import com.karur.access_management_application.security.util.CommonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
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

    @Autowired
    AuthorityRequestToEntityMapper authorityRequestToEntityMapper;

    @Autowired
    RoleRequestToEntityMapper roleRequestToEntityMapper;

    @Autowired
    PermissionRequestToEntityMapper permissionRequestToEntityMapper;

    public Mono<AccessEntity> findAccessEntityByUsername(String username) {
        return fetchAccessEntity(username);
    }

    public Mono<AccessEntity> fetchAccessEntity(String username) {
        return accessEntityRepository.findByUsername(username)
                .doOnSuccess(accessEntity -> log.info("User: {} with details: {}", username, accessEntity))
                .flatMap(accessEntity -> accessAuthorityIdRepository.findByAccessId(accessEntity.getId())
                        .collectList()
                        .flatMap(accessAuthorityEntities -> Flux.fromIterable(accessAuthorityEntities.stream()
                                        .map(AccessAuthorityEntity::getAuthorityId).toList()).flatMap(this::fetchAuthorityEntity)
                                .collectList()
                                .map(authorityEntities -> {
                                    // 1. Mutate the object safely inside the map function
                                    accessEntity.setAuthorityEntities(authorityEntities);
                                    // 2. Pass the modified object down the stream
                                    return accessEntity;
                                })
                        )
                );

    }

    public Mono<AuthorityEntity> fetchAuthorityEntity(Long id) {
        return authorityEntityRepository.findById(id)
                .flatMap(authorityEntity -> authorityRoleIdRepository.findByAuthorityId(authorityEntity.getId())
                        .collectList()
                        .flatMap(authorityRoleEntities -> Flux.fromIterable(authorityRoleEntities.stream().map(AuthorityRoleEntity::getRoleId).toList()).flatMap(this::fetchRoleEntity)
                                .collectList()
                                .doOnNext(authorityEntity::setRoleEntities)
                                .then(Mono.just(authorityEntity))));
    }

    public Mono<RoleEntity> fetchRoleEntity(Long id) {
        return roleEntityRepository.findById(id)
                .flatMap(roleEntity -> rolePermissionIdRepository.findByRoleId(roleEntity.getId())
                        .collectList()
                        .flatMap(rolePermissionEntities -> permissionEntityRepository.findByIdIn(rolePermissionEntities.stream()
                                .map(RolePermissionEntity::getPermissionId).toList())
                                .collectList()
                                .doOnNext(roleEntity::setPermissionEntities)
                                .then(Mono.just(roleEntity)))
                );
    }

    public Mono<AccessEntity> saveAccessEntity(AccessEntity accessEntity) {
        return accessEntityRepository.save(accessEntity)
                .flatMap(accessEntity1 -> Flux.fromIterable(accessEntity1.getAuthorityEntities()).flatMap(this::saveAuthorityEntity)
                        .flatMap(authorityEntity -> accessAuthorityIdRepository.findByAccessIdAndAuthorityId(accessEntity1.getId(), authorityEntity.getId())
                                .switchIfEmpty(Flux.just(authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity1.getId(), authorityEntity)))
                                .flatMap(accessAuthorityEntity -> accessAuthorityIdRepository.save(accessAuthorityEntity)))
                        .then(Mono.just(accessEntity1)));
    }

    public Mono<AuthorityEntity> saveAuthorityEntity(AuthorityEntity authorityEntity) {
        return authorityEntityRepository.save(authorityEntity)
                .flatMap(authorityEntity1 -> Flux.fromIterable(authorityEntity1.getRoleEntities()).flatMap(this::saveRoleEntity)
                        .flatMap(roleEntity -> authorityRoleIdRepository.findByAuthorityIdAndRoleId(authorityEntity1.getId(), roleEntity.getId())
                                .switchIfEmpty(Flux.just(roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity1.getId(), roleEntity)))
                                .flatMap(rolePermissionEntity -> authorityRoleIdRepository.save(rolePermissionEntity)))
                        .then(Mono.just(authorityEntity1)));
    }

    public Mono<RoleEntity> saveRoleEntity(RoleEntity roleEntity) {
        return roleEntityRepository.save(roleEntity)
                .flatMap(roleEntity1 -> Flux.fromIterable(CommonUtil.returnListElseEmpty(roleEntity1.getPermissionEntities())).flatMap(this::savePermissionEntity)
                        .flatMap(permissionEntity -> rolePermissionIdRepository.findByRoleIdAndPermissionId(roleEntity1.getId(), permissionEntity.getId())
                                .switchIfEmpty(Flux.just(permissionRequestToEntityMapper.buildRolePermissionEntity(roleEntity1.getId(), permissionEntity)))
                                .flatMap(rolePermissionEntity -> rolePermissionIdRepository.save(rolePermissionEntity)))
                        .then(Mono.just(roleEntity1)));
    }

    public Mono<PermissionEntity> savePermissionEntity(PermissionEntity permissionEntity) {
        return permissionEntityRepository.save(permissionEntity);
    }



    //mapping starts
    public Flux<AccessAuthorityEntity> buildOnlyAccessAuthorityEntity(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return Flux.fromIterable(authorityEntities).flatMap(authorityEntity -> accessAuthorityIdRepository.findByAccessIdAndAuthorityId(accessEntity.getId(), authorityEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)))));
    }

    public Flux<AuthorityRoleEntity> buildOnlyAuthorityRoleEntity(AuthorityEntity authorityEntity, List<RoleEntity> roleEntities) {
        return Flux.fromIterable(roleEntities).flatMap(roleEntity -> authorityRoleIdRepository.findByAuthorityIdAndRoleId(authorityEntity.getId(), roleEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity.getId(), roleEntity))))
        );
    }

    public Flux<RolePermissionEntity> buildOnlyRolePermissionEntity(RoleEntity roleEntity, List<PermissionEntity> permissionEntities) {
        return Flux.fromIterable(permissionEntities).flatMap(permissionEntity -> rolePermissionIdRepository.findByRoleIdAndPermissionId(roleEntity.getId(), permissionEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildRolePermissionEntity(roleEntity.getId(), permissionEntity)))));
    }
    //mapping ends
}
