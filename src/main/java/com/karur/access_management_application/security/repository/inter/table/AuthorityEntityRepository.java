package com.karur.access_management_application.security.repository.inter.table;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthorityEntityRepository extends ReactiveCrudRepository<AuthorityEntity, Long> {
    Mono<AuthorityEntity> findByName(String name);
}
