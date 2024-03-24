package com.example.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    private Long roleId;
    private String roleName;
    private String roleDescription;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}