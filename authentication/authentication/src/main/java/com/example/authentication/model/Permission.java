package com.example.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    private Long permissionId;
    private String permissionName;
    private String permissionModule;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
