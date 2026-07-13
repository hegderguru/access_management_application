package com.karur.access_management_application.security.repository.inter.table;

import com.karur.access_management_application.security.entity.AccessEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccessEntityRepository extends ReactiveCrudRepository<AccessEntity,Long> {
    Mono<AccessEntity> findByUsername(String username);
}
