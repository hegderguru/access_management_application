package com.karur.access_management_application.security.util;

import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;

import java.util.List;
import java.util.Objects;

public class AccessDetailsUpdateUtil {

    public static List<CompareUtil.Change> getUpdateChanges(List<CompareUtil.Change> changes) {
        return changes.stream().filter(change -> Objects.nonNull(change.getLeft()) && Objects.nonNull(change.getRight()) && Objects.nonNull(change.getRightValue())).toList();
    }

    public static List<CompareUtil.Change> getNewChanges(List<CompareUtil.Change> changes) {
        return changes.stream().filter(change -> Objects.isNull(change.getLeft()) && Objects.isNull(change.getRight()) && Objects.isNull(change.getLeftValue()) && Objects.nonNull(change.getRightValue())).toList();
    }

    public static List<CompareUtil.Change> accessChanges(AccessRequest existing,AccessRequest updates){
        return CompareUtil.compare(existing,updates);
    }

    public static List<CompareUtil.Change> getNewAuthorityRequest(List<CompareUtil.Change> changes){
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(AuthorityRequest.class)).toList();
    }

    public static List<CompareUtil.Change> getNewRoleRequest(List<CompareUtil.Change> changes){
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(RoleRequest.class)).toList();
    }

    public static List<CompareUtil.Change> getNewPermissionRequest(List<CompareUtil.Change> changes){
        return getNewChanges(changes).stream().filter(change -> change.getRightValue().getClass().equals(PermissionRequest.class)).toList();
    }
}
