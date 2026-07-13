package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class RoleRequestToEntityMapper {

    public AuthorityRoleEntity buildAuthorityRoleEntity(Long authorityId, RoleEntity roleEntity) {
        return AuthorityRoleEntity.builder()
                .authorityId(authorityId)
                .roleId(roleEntity.getId())
                .build();
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return RoleEntity.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .build();
    }

    public Mono<Void> saveOrUpdateRolesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return newRolesOnChanges(authorityEntity, changes).then(Mono.defer(() -> updateRolesOnChanges(authorityEntity, changes)));
    }

    public Mono<Void> newRolesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessRequestUpdateUtil.getNewRoleRequest(changes))
                .flatMap(change -> Mono.just(buildRoleEntity((RoleRequest) change.getRightValue())))
                .flatMap(roleEntity -> {
                    authorityEntity.addRoleEntity(roleEntity);
                    return Mono.empty();
                }).then();
    }

    public Mono<Void> updateRolesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        Map<String, List<CompareUtil.Change>> updateRoleRequest1 = AccessRequestUpdateUtil.getUpdateRoleRequest(changes);
        return Flux.fromIterable(updateRoleRequest1.entrySet())
                .flatMap(stringListEntry -> {
                    RoleEntity roleEntity = authorityEntity.getRoleEntities().stream().filter(authorityEntity1 -> authorityEntity1.getName().equalsIgnoreCase(stringListEntry.getKey())).findFirst().get();
                    return updateRoleOnChanges(roleEntity, stringListEntry.getValue());
                }).then();
    }

    private Mono<Void> updateRoleOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(changes)
                .flatMap(change -> {
                    switch (RoleRequest.Fields.valueOf(change.getField())) {
                        case description -> roleEntity.setDescription(ChangeUtil.getStringElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
