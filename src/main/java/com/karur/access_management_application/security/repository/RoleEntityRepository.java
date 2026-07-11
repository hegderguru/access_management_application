package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleEntityRepository extends ReactiveCrudRepository<RoleEntity,Long> {
    Flux<RoleEntity> findByAuthorityId(Long authorityId);
}
