package com.example.authentication.model;

import com.example.authentication.entity.PermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    private Long roleId;
    private String roleName;
    private String roleDescription;
    private Set<PermissionEntity> permission;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}