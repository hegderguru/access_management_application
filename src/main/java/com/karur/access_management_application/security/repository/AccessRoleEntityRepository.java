package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessRoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccessRoleEntityRepository extends ReactiveCrudRepository<AccessRoleEntity,Long> {
    Flux<AccessRoleEntity> findByAuthorityId(Long authorityId);
}
