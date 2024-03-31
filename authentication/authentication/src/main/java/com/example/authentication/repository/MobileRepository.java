package com.example.authentication.repository;

import com.example.authentication.entity.MobileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MobileRepository extends JpaRepository<MobileEntity, Long> {
    @Query(value = "SELECT m.* FROM mobile m " +
            "WHERE m.mob_name =:mobileName", nativeQuery = true)
    Optional<List<MobileEntity>> findAllByMobileName(String mobileName);

    @Query(value = "SELECT m.* FROM mobile m " +
            "WHERE m.mob_type =:mobileType", nativeQuery = true)
    Optional<List<MobileEntity>> findAllByMobileType(String mobileType);
}
