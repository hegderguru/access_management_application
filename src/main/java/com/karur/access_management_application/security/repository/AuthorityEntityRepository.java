package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface AuthorityEntityRepository extends ReactiveCrudRepository<AuthorityEntity, Long> {
    Flux<AuthorityEntity> findByAccessorId(Long accessorId);

    Flux<AuthorityEntity> findByIdIn(List<Long> ids);
}
