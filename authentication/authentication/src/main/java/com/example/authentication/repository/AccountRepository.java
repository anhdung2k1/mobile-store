package com.example.authentication.repository;

import com.example.authentication.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,Long>{
    Optional<AccountEntity> findByUserName(String userName);

    @Query(value = "SELECT ac.* FROM accounts ac " +
                   "INNER JOIN users u ON ac.user_name = u.user_name " +
                   "WHERE u.user_id =:userId", nativeQuery = true)
    Optional<AccountEntity> findByUserId(Long userId);
}
