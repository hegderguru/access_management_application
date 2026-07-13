package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityEntityRepository extends ReactiveCrudRepository<AuthorityEntity, Long> {
}
