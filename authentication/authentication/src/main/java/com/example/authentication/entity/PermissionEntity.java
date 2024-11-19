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
@Table(name = "PERMISSION")
@Transactional(rollbackOn = Exception.class)
public class PermissionEntity implements Serializable {
    public PermissionEntity() {
        this.permissionName = "READ";
        this.permissionModule = "INVENTORY;SALES;CUSTOMER";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    public PermissionEntity(String permissionName, String permissionModule) {
        this.permissionName = permissionName;
        this.permissionModule = permissionModule;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PER_ID", nullable = false, unique = true)
    @TableGenerator(name = "PER_GEN",
                    table = "SEQUENCER",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT",
                    pkColumnValue = "PER_SEQ_NEXT_VAL",
                    allocationSize = 1)
    private Long permissionId;

    @Column(name = "PER_NAME", nullable = false, length = 64)
    @NotBlank(message = "Permission Name must not be blank")
    private String permissionName;

    @Column(name = "PER_MODULE", nullable = false, length = 128)
    @NotBlank(message = "Permission Module must at least specify one module")
    private String permissionModule;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
