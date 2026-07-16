package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
                .fullyQualifiedFieldName(permissionRequest.getFullyQualifiedFieldName())
                .read_(permissionRequest.getRead())
                .create_(permissionRequest.getCreate())
                .update_(permissionRequest.getUpdate())
                .delete_(permissionRequest.getDelete())
                .build();
    }

    private Mono<Void> updatePermissionOnChanges(PermissionEntity permissionEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(changes)
                .flatMap(change -> {
                    switch (PermissionRequest.Fields.valueOf(change.getField())) {
                        case read -> permissionEntity.setRead_(ChangeUtil.getBooleanElseConvert(change));
                        case create -> permissionEntity.setCreate_(ChangeUtil.getBooleanElseConvert(change));
                        case update -> permissionEntity.setUpdate_(ChangeUtil.getBooleanElseConvert(change));
                        case delete -> permissionEntity.setDelete_(ChangeUtil.getBooleanElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
