package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessorEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccessorEntityRepository extends ReactiveCrudRepository<AccessorEntity,Long> {

    Mono<AccessorEntity> findByUsername(String username);
}
