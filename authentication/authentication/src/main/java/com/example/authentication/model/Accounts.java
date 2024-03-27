package com.example.authentication.model;

import java.time.LocalDateTime;

import com.example.authentication.entity.RoleEntity;
import com.example.authentication.entity.UserEntity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {
    private Long accountId;
    private RoleEntity roles;
    private String userName;
    private String password;
    private UserEntity users;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
