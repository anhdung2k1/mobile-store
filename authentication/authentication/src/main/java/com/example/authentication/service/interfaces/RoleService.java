package com.example.authentication.service.interfaces;

import com.example.authentication.model.Roles;

import java.util.List;
import java.util.Map;

public interface RoleService {
    Roles createRole(Roles role) throws Exception;
    List<Map<String, String>> getAllRoles();
    Map<String, String> getRoleByName(String roleName) throws Exception;
    Roles updateRole(Long roleId, Roles role) throws Exception;
    Boolean deleteRole(Long roleId) throws Exception;
}
