package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessorEntity;
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

   /* public AccessorEntity findAccessorEntityByUsername(String username){
        return accessorEntityRepository.findByUsername(username)
                .flatMap(accessorEntity -> {
                    return accessorAuthorityEntityRepository.findByAccessorId(accessorEntity.getId())
                            .collectList()
                            .doOnNext(accessorEntity::setAccessGrantedAuthorities)
                            .thenReturn(accessorEntity));
                }).flatMap(accessorEntity -> {
                    Flux.fromIterable(accessorEntity.getAccessGrantedAuthorities())
                            .flatMap(accessGrantedAuthority -> {
                                return accessRoleEntityRepository.findByAuthorityId(accessGrantedAuthority.getId())
                                        .collectList()
                                        .doOnNext(accessGrantedAuthority::setAccessRoleEntities)
                                        .thenReturn(accessorEntity);
                            })
                            .collectList()
                            .doOnNext(accessRoleEntities -> accessGrantedAuth)
                    return accessRoleEntityRepository.findByAuthorityId()
                })

    }*/

    /*public AccessorEntity findAccessorEntityByUsername(String username){
        return accessorEntityRepository.findByUsername(username)
                .flatMap(accessorEntity -> {
                    return accessorAuthorityEntityRepository.findByAccessorId(accessorEntity.getId())
                            .collectList()
                            .flatMap(accessGrantedAuthorities -> {
                                accessorEntity.setAccessGrantedAuthorities(accessGrantedAuthorities);
                                return Flux.fromIterable(accessGrantedAuthorities)
                                        .flatMap(accessGrantedAuthority -> {
                                            return accessRoleEntityRepository.findByAuthorityId(accessGrantedAuthority.getId())
                                                    .collectList()
                                                    .flatMap(accessRoleEntities -> {
                                                        accessGrantedAuthority.setAccessRoleEntities(accessRoleEntities);
                                                        Flux.fromIterable(accessRoleEntities)
                                                                .flatMap(accessRoleEntity -> {
                                                                    return accessPermissionEntityRepository.findByRoleId(accessRoleEntity.getId())
                                                                            .collectList()
                                                                            .flatMap(accessPermissionEntities -> {
                                                                                accessRoleEntity.setAccessPermissionEntities(accessPermissionEntities);
                                                                            })
                                                                })
                                                    })
                                        })
                            })
                })
    }*/

    public Mono<AccessorEntity> findAccessorEntityByUsername(String username) {
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

}
