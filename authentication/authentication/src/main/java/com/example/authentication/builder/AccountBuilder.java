package com.example.authentication.builder;

import com.example.authentication.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Builder
@Data
@Table(name = "ACCOUNTS")
@NoArgsConstructor
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AccountBuilder implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACC_ID", nullable = false, unique = true)
    @TableGenerator(name = "ACCOUNT_GEN",
                    table = "SEQUENCER",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT",
                    pkColumnValue = "ACCOUNT_SEQ_NEXT_VAL",
                    allocationSize = 1)
    private Long acc_id;
    @Column(name = "USER_NAME", nullable = false, length = 20)
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, message = "Username must at least 3 characters")
    private String userName;

    @Column(name = "HASH_PASS", nullable = false, length = 64)
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private RoleEntity roles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.getAuthorities();
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
