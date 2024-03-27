package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ROLES")
@Transactional(rollbackOn = Exception.class)
public class RoleEntity implements Serializable {
    public RoleEntity() {
        this.roleName = "USER";
        this.roleDescription = "User only view permission";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    public RoleEntity(String roleName) {
        this.roleName = roleName;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", nullable = false, unique = true)
    @TableGenerator(name = "ROLE_GEN",
                    table = "SEQUENCER",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT",
                    pkColumnValue = "ROLE_SEQ_NEXT_VAL",
                    allocationSize = 1)
    private Long roleId;

    @Column(name = "ROLE_NAME", nullable = false, length = 20)
    @NotBlank(message = "Role Name must not be blank")
    private String roleName;

    @Column(name = "ROLE_DESCRIPTION", length = 64)
    private String roleDescription;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}