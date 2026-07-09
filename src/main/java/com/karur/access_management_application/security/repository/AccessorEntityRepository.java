package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessorEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessorEntityRepository extends ReactiveCrudRepository<AccessorEntity,String> {
}
