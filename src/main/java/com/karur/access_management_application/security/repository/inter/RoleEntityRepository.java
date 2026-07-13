package com.karur.access_management_application.security.repository.inter;

import com.karur.access_management_application.security.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends ReactiveCrudRepository<RoleEntity,Long> {
}
