package com.example.authentication.service.interfaces;

import com.example.authentication.model.Permission;

import java.util.*;

public interface PermissionService {
    Permission createPermission(Permission permission) throws Exception;
    List<Map<String, Object>> getAllPermission();
    Map<String, Object> getPermissionByName(String permissionName) throws Exception;
    Permission updatePermission(Long permissionId, Permission permission) throws Exception;
    Boolean deletePermission(Long permissionId) throws Exception;
}
