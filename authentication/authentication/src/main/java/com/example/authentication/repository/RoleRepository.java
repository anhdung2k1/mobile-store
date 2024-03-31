package com.example.authentication.repository;

import com.example.authentication.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query(value = "SELECT r.* FROM roles r " +
            "WHERE r.role_name =:roleName", nativeQuery = true)
    Optional<RoleEntity> findByRoleName(String roleName);
}
