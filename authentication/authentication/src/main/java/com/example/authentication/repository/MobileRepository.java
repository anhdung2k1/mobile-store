package com.example.authentication.repository;

import com.example.authentication.entity.MobileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MobileRepository extends JpaRepository<MobileEntity, Long> {
    @Query(value = "SELECT m.* FROM mobile m " +
            "WHERE m.mob_name LIKE %:mobileName% " +
            "LIMIT 28", nativeQuery = true)
    Optional<List<MobileEntity>> findAllByMobileNameContains(@Param("mobileName") String mobileName);

    @Query(value = "SELECT m.* FROM mobile m " +
            "WHERE m.mob_type LIKE %:mobileType% " +
            "LIMIT 28", nativeQuery = true)
    Optional<List<MobileEntity>> findAllByMobileTypeContains(@Param("mobileType") String mobileType);

    @Query(value = "SELECT distinct m.mob_type FROM mobile m", nativeQuery = true)
    Optional<List<String>> findMobileCategories();
}
