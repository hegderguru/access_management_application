package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class PermissionRequestToEntityMapper {

    public RolePermissionEntity buildRolePermissionEntity(Long roleId, PermissionEntity permissionEntity) {
        return RolePermissionEntity.builder()
                .roleId(roleId)
                .permissionId(permissionEntity.getId())
                .build();
    }

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return PermissionEntity.builder()
                .classPath(permissionRequest.getClassPath())
                .className(permissionRequest.getClassName())
                .fieldName(permissionRequest.getFieldName())
                .read(permissionRequest.getRead())
                .create(permissionRequest.getCreate())
                .update(permissionRequest.getUpdate())
                .delete(permissionRequest.getDelete())
                .build();
    }

    public Mono<Void> saveOrUpdatePermissionsOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return newPermissionsOnChanges(roleEntity, changes).then(Mono.defer(() -> updatePermissionsOnChanges(roleEntity, changes)));
    }

    public Mono<Void> newPermissionsOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessRequestUpdateUtil.getNewPermissionRequest(changes))
                .flatMap(change -> Mono.just(buildPermissionEntity((PermissionRequest) change.getRightValue())))
                .flatMap(permissionEntity -> {
                    roleEntity.addPermissionEntity(permissionEntity);
                    return Mono.empty();
                }).then();
    }

    public Mono<Void> updatePermissionsOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        Map<String, List<CompareUtil.Change>> updateRoleRequest1 = AccessRequestUpdateUtil.getUpdatePermissionRequest(changes);
        return Flux.fromIterable(updateRoleRequest1.entrySet())
                .flatMap(stringListEntry -> {
                    PermissionEntity permissionEntity = roleEntity.getPermissionEntities().stream().filter(authorityEntity1 -> authorityEntity1.fullyQualifiedFieldPath().equalsIgnoreCase(stringListEntry.getKey())).findFirst().get();
                    return updatePermissionOnChanges(permissionEntity, stringListEntry.getValue());
                }).then();
    }

    private Mono<Void> updatePermissionOnChanges(PermissionEntity permissionEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(changes)
                .flatMap(change -> {
                    switch (PermissionRequest.Fields.valueOf(change.getField())) {
                        case read -> permissionEntity.setRead(ChangeUtil.getBooleanElseConvert(change));
                        case create -> permissionEntity.setCreate(ChangeUtil.getBooleanElseConvert(change));
                        case update -> permissionEntity.setUpdate(ChangeUtil.getBooleanElseConvert(change));
                        case delete -> permissionEntity.setDelete(ChangeUtil.getBooleanElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
