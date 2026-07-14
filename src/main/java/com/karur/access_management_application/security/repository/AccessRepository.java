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
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
                .flatMap(accessEntity -> accessAuthorityIdRepository.findByAccessId(accessEntity.getId())
                        .map(AccessAuthorityEntity::getAuthorityId) // Extract IDs directly within the Flux stream
                        .flatMap(this::fetchAuthorityEntity)      // Executes correctly if IDs are present
                        .collectList()                       // Safe to collect even if empty
                        .map(authorityEntities -> {
                            accessEntity.setAuthorityEntities(authorityEntities);
                            return accessEntity;
                        })
                );
    }

    public Mono<AuthorityEntity> fetchAuthorityEntity(Long id) {
        return authorityEntityRepository.findById(id)
                .doOnRequest(value -> log.info("id: {}", id))
                .flatMap(authorityEntity -> authorityRoleIdRepository.findByAuthorityId(authorityEntity.getId())
                        .map(AuthorityRoleEntity::getRoleId) // Extract IDs directly within the Flux stream
                        .flatMap(this::fetchRoleEntity)      // Executes correctly if IDs are present
                        .collectList()                       // Safe to collect even if empty
                        .map(roleEntities -> {
                            authorityEntity.setRoleEntities(roleEntities);
                            return authorityEntity;
                        })
                );
    }

    public Mono<RoleEntity> fetchRoleEntity(Long id) {
        return roleEntityRepository.findById(id)
                .doOnRequest(value -> log.info("id: {}", id))
                .flatMap(roleEntity -> rolePermissionIdRepository.findByRoleId(roleEntity.getId())
                        .map(RolePermissionEntity::getPermissionId) // Extract IDs directly within the Flux stream
                        .flatMap(permissionEntityRepository::findById)      // Executes correctly if IDs are present
                        .collectList()                       // Safe to collect even if empty
                        .map(permissionEntities -> {
                            roleEntity.setPermissionEntities(permissionEntities);
                            return roleEntity;
                        })
                );
    }

    public Mono<AccessEntity> saveAccessEntity(AccessEntity accessEntity) {
        return accessEntityRepository.save(accessEntity)
                .flatMap(savedAccessEntity -> {
                    List<AuthorityEntity> incomingAuthorities = CommonUtil.returnListElseEmpty(savedAccessEntity.getAuthorityEntities());
                    return getAuthorityEntityFlux(savedAccessEntity, incomingAuthorities)
                            .then(Mono.just(savedAccessEntity)); // Emits clean, duplicate-free graph
                });
    }

    private @NonNull Flux<AuthorityEntity> getAuthorityEntityFlux(AccessEntity savedAccessEntity, List<AuthorityEntity> authorityEntities) {
        return authorityEntityRepository.saveAll(authorityEntities)
                .flatMap(savedAuthorityEntity -> {
                    List<RoleEntity> roleEntities = CommonUtil.returnListElseEmpty(savedAuthorityEntity.getRoleEntities());
                    savedAccessEntity.addAuthorityEntity(savedAuthorityEntity);
                    return accessAuthorityIdRepository.findByAccessIdAndAuthorityId(savedAccessEntity.getId(), savedAuthorityEntity.getId())
                            .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAccessAuthorityEntity(savedAccessEntity.getId(), savedAuthorityEntity))))
                            .flatMap(accessAuthorityIdRepository::save)
                            .then(saveRoleEntities(savedAuthorityEntity, roleEntities))
                            .then(Mono.just(savedAuthorityEntity));
                });
    }

    private @NonNull Mono<Void> saveRoleEntities(AuthorityEntity savedAuthorityEntity, List<RoleEntity> roleEntities) {
        return roleEntityRepository.saveAll(roleEntities)
                .flatMap(savedRoleEntity -> {
                    List<PermissionEntity> permissionEntities = CommonUtil.returnListElseEmpty(savedRoleEntity.getPermissionEntities());
                    savedAuthorityEntity.addRoleEntity(savedRoleEntity);
                    return authorityRoleIdRepository.findByAuthorityIdAndRoleId(savedAuthorityEntity.getId(), savedRoleEntity.getId())
                            .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildAuthorityRoleEntity(savedAuthorityEntity.getId(), savedRoleEntity))))
                            .flatMap(authorityRoleIdRepository::save)
                            .then(savePermissionEntities(savedRoleEntity, permissionEntities))
                            .then(Mono.just(savedRoleEntity));
                })
                .then();
    }

    private Mono<Void> savePermissionEntities(RoleEntity savedRoleEntity, List<PermissionEntity> permissionEntities) {
        return permissionEntityRepository.saveAll(permissionEntities)
                .flatMap(savedPermissionEntity -> {
                    savedRoleEntity.addPermissionEntity(savedPermissionEntity);
                    return rolePermissionIdRepository.findByRoleIdAndPermissionId(savedRoleEntity.getId(), savedPermissionEntity.getId())
                            .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildRolePermissionEntity(savedRoleEntity.getId(), savedPermissionEntity))))
                            .flatMap(rolePermissionIdRepository::save)
                            .then(Mono.just(savedPermissionEntity));
                })
                .then();
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
