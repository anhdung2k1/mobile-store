package com.example.authentication.repository;

import com.example.authentication.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    @Query(value = "SELECT p.* FROM permission p " +
            "WHERE p.per_name =:permissionName", nativeQuery = true)
    Optional<PermissionEntity> findByPermissionName(String permissionName);
}
