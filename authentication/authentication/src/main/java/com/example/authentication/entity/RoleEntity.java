package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "ROLES")
@Transactional(rollbackOn = Exception.class)
public class RoleEntity implements Serializable {
    public RoleEntity() {
        this.roleName = "USER";
        this.roleDescription = "User have only create permission";
        this.permissions = new HashSet<>(){{
            add(new PermissionEntity("READ", "INVENTORY;SALES;CUSTOMER"));
            add(new PermissionEntity("CREATE", "INVENTORY;SALES;CUSTOMER"));
        }};
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    public RoleEntity(String roleName) {
        this.roleName = roleName;
        if (this.roleName.equalsIgnoreCase("USER")) {
            this.roleDescription = "User have CREATE and READ permission";
            this.permissions = new HashSet<>() {{
                add(new PermissionEntity("READ", "INVENTORY;SALES;CUSTOMER"));
                add(new PermissionEntity("CREATE", "INVENTORY;SALES;CUSTOMER"));
                add(new PermissionEntity("UPDATE", "INVENTORY;SALES;CUSTOMER"));
                add(new PermissionEntity("DELETE", "INVENTORY;SALES;CUSTOMER"));
            }};
        }
        else {
            this.roleDescription = "ADMIN have all permissions";
            this.permissions = new HashSet<>(){{
                add(new PermissionEntity("READ", "INVENTORY;SALES;CUSTOMER;MANAGE"));
                add(new PermissionEntity("CREATE", "INVENTORY;SALES;CUSTOMER;MANAGE"));
                add(new PermissionEntity("UPDATE", "INVENTORY;SALES;CUSTOMER;MANAGE"));
                add(new PermissionEntity("DELETE", "INVENTORY;SALES;CUSTOMER;MANAGE"));
            }};
        }
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
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

    @ManyToMany
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PER_ID")
    )
    private Set<PermissionEntity> permissions;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permissions -> new SimpleGrantedAuthority(permissions.getPermissionName()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.roleName));
        return authorities;
    }
}