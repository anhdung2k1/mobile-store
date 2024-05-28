package com.example.authentication.repository;

import com.example.authentication.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    @Query(value =  "SELECT c.* FROM carts c " +
                    "WHERE c.user_id =:userId ",
                    nativeQuery = true)
    Optional<CartEntity> getCartEntityWithUserId(@Param("userId")Long userId);
}