package com.karur.access_management_application.security.repository.inter.table;

import com.karur.access_management_application.security.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleEntityRepository extends ReactiveCrudRepository<RoleEntity,Long> {
    Mono<RoleEntity> findByName(String name);
}
