package com.example.authentication.repository;

import com.example.authentication.entity.MobileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileRepository extends JpaRepository<MobileEntity, Long> {

}
