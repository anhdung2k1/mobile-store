package com.example.authentication.repository;

import com.example.authentication.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = "SELECT o.* FROM orders o LIMIT 30", nativeQuery = true)
    Optional<List<OrderEntity>> findAllOrders();

    @Query(value = "SELECT o.* FROM orders o WHERE o.user_id =:userID", nativeQuery = true)
    Optional<List<OrderEntity>> findAllOrdersByUserID(Long userID);
}
