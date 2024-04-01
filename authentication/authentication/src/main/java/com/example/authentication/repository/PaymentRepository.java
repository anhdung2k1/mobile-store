package com.example.authentication.repository;

import com.example.authentication.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query(value = "SELECT p.* FROM payment p " +
            "WHERE p.pay_id in (" +
            "SELECT pu.pay_id FROM customer_payment pu " +
            "WHERE pu.cus_id =:customerId)", nativeQuery = true)
    Optional<List<PaymentEntity>> findAllPaymentByCustomerId(Long customerId);

    @Query(value = "SELECT p.* FROM payment p " +
            "WHERE p.mob_id =:mobileId", nativeQuery = true)
    Optional<List<PaymentEntity>> findAllPaymentByMobileId(Long mobileId);
}
