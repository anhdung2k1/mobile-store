package com.example.authentication.repository;

import com.example.authentication.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    @Query(value =  "SELECT r.* FROM rating r " +
                    "INNER JOIN mobile mb on mb.rating_id = r.rating_id " +
                    "WHERE mb.mob_id =:mobileID " +
                    "LIMIT 1"
            ,nativeQuery = true)
    Optional<RatingEntity> findRatingEntityWithMobileID(Long mobileID);
}
