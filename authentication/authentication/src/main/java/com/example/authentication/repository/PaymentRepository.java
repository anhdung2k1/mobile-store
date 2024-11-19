package com.example.authentication.repository;

import com.example.authentication.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query(value = "SELECT p.* FROM payment p " +
            "WHERE p.pay_id in (" +
            "SELECT pu.pay_id FROM user_payment pu " +
            "WHERE pu.user_id =:userId)", nativeQuery = true)
    Optional<List<PaymentEntity>> findAllPaymentByUserId(Long userId);

    Optional<PaymentEntity> findPaymentEntitiesByPaymentMethod(String paymentMethod);
}
