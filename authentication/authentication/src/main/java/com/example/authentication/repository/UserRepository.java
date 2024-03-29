package com.example.authentication.repository;

import com.example.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    List<UserEntity> findByUserNameContains(String userName);
    
    @Query(value = "select u.* from users u where u.user_name =:userName",nativeQuery=true)
    Optional<UserEntity> findByUserName(String userName);
}

