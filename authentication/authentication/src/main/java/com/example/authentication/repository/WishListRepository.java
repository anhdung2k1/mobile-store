package com.example.authentication.repository;

import com.example.authentication.entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {
    @Query(value =  "SELECT w.* FROM wishlist w " +
            "WHERE w.user_id =:userId ",
            nativeQuery = true)
    Optional<WishListEntity> getWishListEntityWithUserId(@Param("userId")Long userId);
}
