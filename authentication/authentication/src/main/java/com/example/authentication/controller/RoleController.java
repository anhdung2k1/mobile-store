package com.example.authentication.controller;

import com.example.authentication.model.Roles;
import com.example.authentication.service.interfaces.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    // Create new role
    @PostMapping(value = "/roles")
    public ResponseEntity<Roles> createRole(@RequestBody Roles role) throws Exception {
        return ResponseEntity.ok(roleService.createRole(role));
    }
    // Get all roles
    @GetMapping(value = "/roles")
    public ResponseEntity<List<Map<String, String>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    // Get Role By Role Name
    @GetMapping(value = "/roles/query")
    public ResponseEntity<Map<String, String>> getRoleByName(@RequestParam("query") String roleName) throws  Exception {
        return ResponseEntity.ok(roleService.getRoleByName(roleName));
    }
    //Update Role
    @PatchMapping(value = "/roles/{roleId}")
    public ResponseEntity<Roles> updateRole(@PathVariable Long roleId, @RequestBody Roles role) throws Exception {
        return ResponseEntity.ok(roleService.updateRole(roleId, role));
    }
    // Delete Role By Role Id
    @DeleteMapping(value = "/roles/{roleId}")
    public ResponseEntity<Map<String,Boolean>> deleteRole(@PathVariable Long roleId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted",roleService.deleteRole(roleId));
        }});
    }
}
