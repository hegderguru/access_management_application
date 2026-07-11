package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AccessorRepository {

    @Autowired
    AccessorEntityRepository accessorEntityRepository;

    @Autowired
    AccessorAuthorityEntityRepository accessorAuthorityEntityRepository;

    @Autowired
    AccessRoleEntityRepository accessRoleEntityRepository;

    @Autowired
    AccessPermissionEntityRepository accessPermissionEntityRepository;

    public Mono<AccessEntity> findAccessorEntityByUsername(String username) {
        return accessorEntityRepository.findByUsername(username)
                .flatMap(accessorEntity ->
                        accessorAuthorityEntityRepository.findByAccessorId(accessorEntity.getId())
                                .collectList()
                                .flatMap(accessGrantedAuthorities -> {
                                    accessorEntity.setAccessGrantedAuthorities(accessGrantedAuthorities);
                                    // Map over each authority to fetch its roles reactively
                                    return Flux.fromIterable(accessGrantedAuthorities)
                                            .flatMap(accessGrantedAuthority ->
                                                    accessRoleEntityRepository.findByAuthorityId(accessGrantedAuthority.getId())
                                                            .collectList()
                                                            .flatMap(accessRoleEntities -> {
                                                                accessGrantedAuthority.setAccessRoleEntities(accessRoleEntities); // Note: renamed setter to match standard naming convention
                                                                // Map over each role to fetch its permissions reactively
                                                                return Flux.fromIterable(accessRoleEntities)
                                                                        .flatMap(accessRoleEntity ->
                                                                                accessPermissionEntityRepository.findByRoleId(accessRoleEntity.getId())
                                                                                        .collectList()
                                                                                        .doOnNext(accessRoleEntity::setAccessPermissionEntities
                                                                                        )
                                                                        )
                                                                        .then(Mono.just(accessGrantedAuthority)); // Return authority after inner processing finishes
                                                            })
                                            )
                                            .then(Mono.just(accessorEntity)); // Return the complete root entity after everything is assembled
                                })
                );
    }

    public Mono<AccessEntity> save(AccessEntity accessEntity) {
        return accessorEntityRepository.save(accessEntity)
                .flatMap(accessorEntity1 -> {
                    accessorEntity1.accessGrantedAuthorities().forEach(accessGrantedAuthorityEntity -> accessGrantedAuthorityEntity.setAccessorId(accessorEntity1.getId()));
                    return accessorAuthorityEntityRepository.saveAll(accessEntity.accessGrantedAuthorities()).then(Mono.just(accessorEntity1));
                });
    }

    public Mono<AuthorityEntity> save(AuthorityEntity authorityEntity) {
        return accessorAuthorityEntityRepository.save(authorityEntity)
                .flatMap(accessGrantedAuthorityEntity1 -> {
                    accessGrantedAuthorityEntity1.getAccessRoleEntities().forEach(accessRoleEntity -> accessRoleEntity.setAuthorityId(accessGrantedAuthorityEntity1.getId()));
                    return accessRoleEntityRepository.saveAll(accessGrantedAuthorityEntity1.getAccessRoleEntities()).then(Mono.just(accessGrantedAuthorityEntity1));
                });
    }
}
