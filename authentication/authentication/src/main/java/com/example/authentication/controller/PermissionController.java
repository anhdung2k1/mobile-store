package com.example.authentication.controller;

import com.example.authentication.model.Permission;
import com.example.authentication.service.interfaces.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    // Create new Permission
    @PostMapping(value = "/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) throws Exception {
        return ResponseEntity.ok(permissionService.createPermission(permission));
    }
    // Get all Permission
    @GetMapping(value = "/permissions")
    public ResponseEntity<List<Map<String, Object>>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermission());
    }
    // Get Permission By Permission Name
    @GetMapping(value = "/permissions/query")
    public ResponseEntity<Map<String, Object>> getPermissionByName(@RequestParam("query") String roleName) throws  Exception {
        return ResponseEntity.ok(permissionService.getPermissionByName(roleName));
    }
    //Update Permission
    @PatchMapping(value = "/permissions/{permissionId}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long permissionId, @RequestBody Permission permission) throws Exception {
        return ResponseEntity.ok(permissionService.updatePermission(permissionId, permission));
    }
    // Delete Permission By Permission ID
    @DeleteMapping(value = "/permissions/{permissionId}")
    public ResponseEntity<Map<String,Boolean>> deleteRole(@PathVariable("permissionId") Long permissionId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted",permissionService.deletePermission(permissionId));
        }});
    }
}
