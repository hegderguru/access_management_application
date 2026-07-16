package com.karur.access_management_application.security.util;

import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccessRequestUpdateUtil {

    public static List<CompareUtil.Change> getUpdateChanges(List<CompareUtil.Change> changes) {
        return changes.stream().filter(change -> Objects.nonNull(change.getLeft()) && Objects.nonNull(change.getRight()) && Objects.nonNull(change.getRightValue())).toList();
    }

    public static List<CompareUtil.Change> getNewChanges(List<CompareUtil.Change> changes) {
        return changes.stream().filter(change -> ((Objects.isNull(change.getLeft()) ^ Objects.isNull(change.getRight())) || (Objects.isNull(change.getLeftValue()) ^ Objects.nonNull(change.getRightValue())))
        && (Objects.isNull(change.getLeftValue()) && Objects.nonNull(change.getRightValue()))).toList();
    }

    public static List<CompareUtil.Change> accessUpdateChanges(Object existing, Object updates) {
        return accessChanges(existing, updates).stream().filter(change -> Objects.nonNull(change.getRightValue()) || Objects.nonNull(change.getRight())).toList();
    }

    public static List<CompareUtil.Change> accessChanges(Object existing, Object updates) {
        return CompareUtil.compare(existing, updates);
    }

    public static List<CompareUtil.Change> getNewAuthorityRequest(List<CompareUtil.Change> changes) {
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(AuthorityRequest.class)).toList();
    }

    public static List<CompareUtil.Change> getNewRoleRequest(List<CompareUtil.Change> changes) {
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(RoleRequest.class)).toList();
    }

    public static List<CompareUtil.Change> getNewPermissionRequest(List<CompareUtil.Change> changes) {
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(PermissionRequest.class)).toList();
    }

    public static Map<String, List<CompareUtil.Change>> getUpdateAuthorityRequest(List<CompareUtil.Change> changes) {
        return getUpdateChanges(changes).stream().filter(change -> change.getRight().getClass().equals(AuthorityRequest.class) && Objects.nonNull(change.getRightValue()))
                .collect(Collectors.groupingBy(change -> ((AuthorityRequest) change.getRight()).getName()));
    }

    public static Map<String, List<CompareUtil.Change>> getUpdateRoleRequest(List<CompareUtil.Change> changes) {
        return getUpdateChanges(changes).stream().filter(change -> change.getRight().getClass().equals(RoleRequest.class) && Objects.nonNull(change.getRightValue()) )
                .collect(Collectors.groupingBy(change -> ((RoleRequest) change.getRight()).getName()));
    }

    public static Map<String, List<CompareUtil.Change>> getUpdatePermissionRequest(List<CompareUtil.Change> changes) {
        return getUpdateChanges(changes).stream().filter(change -> change.getRight().getClass().equals(PermissionRequest.class)  && Objects.nonNull(change.getRightValue()))
                .collect(Collectors.groupingBy(change -> ((PermissionRequest) change.getRight()).fullyQualifiedFieldPath()));
    }

    public static List<CompareUtil.Change> getUpdateAccessRequest(List<CompareUtil.Change> changes) {
        return getUpdateChanges(changes).stream().filter(change -> change.getRight().getClass().equals(AccessRequest.class)  && Objects.nonNull(change.getRightValue())).toList();
    }
}
