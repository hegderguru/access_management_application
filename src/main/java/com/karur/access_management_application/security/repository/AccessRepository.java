package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.AuthorityRequestToEntityMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.PermissionRequestToEntityMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.RoleRequestToEntityMapper;
import com.karur.access_management_application.security.model.request.PermissionRequest;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Mono<AuthorityEntity> fetchAuthorityEntity(String name) {
        return authorityEntityRepository.findByName(name)
                .doOnRequest(value -> log.info("id: {}", name))
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

    public Mono<RoleEntity> fetchRoleEntity(String name) {
        return roleEntityRepository.findByName(name)
                .doOnRequest(value -> log.info("id: {}", name))
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

    public Mono<AuthorityEntity> saveAuthorityEntity(AuthorityEntity authorityEntity) {
        return authorityEntityRepository.save(authorityEntity)
                .flatMap(savedAuthorityEntity -> {
                    List<RoleEntity> roleEntities = CommonUtil.returnListElseEmpty(savedAuthorityEntity.getRoleEntities());
                    return saveRoleEntities(savedAuthorityEntity, roleEntities)
                            .then(Mono.just(savedAuthorityEntity)); // Emits clean, duplicate-free graph
                });
    }

    public Mono<RoleEntity> saveRoleEntity(RoleEntity roleEntity) {
        return roleEntityRepository.save(roleEntity)
                .flatMap(savedRoleEntity -> {
                    List<PermissionEntity> permissionEntities = CommonUtil.returnListElseEmpty(roleEntity.getPermissionEntities());
                    return savePermissionEntities(roleEntity, permissionEntities)
                            .then(Mono.just(roleEntity)); // Emits clean, duplicate-free graph
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

    public Mono<Void> updateAccessAuthorityEntity(Long accessId, String authorityName) {
        return authorityEntityRepository.findByName(authorityName)
                .flatMap(authorityEntity -> accessAuthorityIdRepository.findByAccessIdAndAuthorityId(accessId, authorityEntity.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessId, authorityEntity))))
                        .flatMap(accessAuthorityIdRepository::save)
                        .then(Mono.empty()));
    }

    public Mono<Void> updateAuthorityRoleEntity(Long authorityId, String roleName) {
        return roleEntityRepository.findByName(roleName)
                .flatMap(roleEntity -> authorityRoleIdRepository.findByAuthorityIdAndRoleId(authorityId, roleEntity.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityId, roleEntity))))
                        .flatMap(authorityRoleIdRepository::save)
                        .then(Mono.empty()));
    }

    public Mono<Void> updateRolePermissionEntity(Long roleId, PermissionRequest permissionRequest) {
        return permissionEntityRepository.findByFullyQualifiedFieldNameAndRead_AndCreate_AndUpdate_AndDelete(permissionRequest.getFullyQualifiedFieldName(),permissionRequest.getRead(),permissionRequest.getCreate(),permissionRequest.getUpdate(),permissionRequest.getDelete())
                .flatMap(permissionEntity -> rolePermissionIdRepository.findByRoleIdAndPermissionId(roleId, permissionEntity.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildRolePermissionEntity(roleId, permissionEntity))))
                        .flatMap(rolePermissionIdRepository::save)
                        .then(Mono.empty()));
    }

    public Mono<Void> createPermissions(List<Boolean[]> permissions, List<Object> objects) {
        return Flux.fromIterable(objects)
                .flatMap(object -> createPermissionOnObject(permissions, object)).then();
    }

    private Flux<Void> createPermissionOnObject(List<Boolean[]> permissions, Object object) {
        return Flux.fromIterable(Arrays.stream(object.getClass().getDeclaredFields()).toList())
                .flatMap(field -> createPermissionOnField(permissions, field))
                .flatMap(permissionEntities -> permissionEntityRepository.saveAll(permissionEntities).then());
    }

    private static Mono<List<PermissionEntity>> createPermissionOnField(List<Boolean[]> permissions, Field field) {
        return Flux.fromIterable(permissions)
                .flatMap(booleans -> {
                    String name = field.getDeclaringClass().getName()+"."+field.getName();
                    return Mono.just(PermissionEntity.builder().fullyQualifiedFieldName(name).read_(booleans[0]).create_(booleans[1]).update_(booleans[2]).delete_(booleans[3]).build());
                })
                .collectList();
    }
}
