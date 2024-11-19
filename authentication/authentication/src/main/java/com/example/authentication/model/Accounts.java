package com.example.authentication.model;

import com.example.authentication.entity.RoleEntity;
import com.example.authentication.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {
    public Accounts(String userName, String password, RoleEntity roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }
    private Long accountId;
    private RoleEntity roles;
    private String userName;
    private String password;
    private UserEntity users;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
