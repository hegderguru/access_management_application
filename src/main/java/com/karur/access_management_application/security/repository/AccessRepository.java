package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
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
    AuthorityRequestToEntityMapper authorityRequestToEntityMapper;

    @Autowired
    RoleRequestToEntityMapper roleRequestToEntityMapper;

    @Autowired
    PermissionRequestToEntityMapper permissionRequestToEntityMapper;

    /*Fetch Access Entity and nested entities*/
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
    /*Fetch Access Entity and nested entities*/

    /*Save Access Entity and nested entities*/
    public Mono<AccessEntity> saveAccessEntity(AccessEntity accessEntity) {
        return accessEntityRepository.save(accessEntity)
                .flatMap(savedAccessEntity -> {
                    List<AuthorityEntity> authorityEntities = CommonUtil.returnListElseEmpty(savedAccessEntity.getAuthorityEntities());
                    return saveAuthorityEntities(savedAccessEntity, authorityEntities)
                            .then(Mono.just(savedAccessEntity)); // Emits clean, duplicate-free graph
                });
    }

    private @NonNull Flux<AuthorityEntity> saveAuthorityEntities(AccessEntity savedAccessEntity, List<AuthorityEntity> authorityEntities) {
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
    /*Save Access Entity and nested entities*/
}
