package com.example.authentication.repository;

import com.example.authentication.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query(value = "SELECT trans.* FROM transactions trans " +
            "WHERE trans.pay_id in (" +
            "SELECT pu.pay_id FROM customer_payment pu " +
            "WHERE pu.cus_id =:customerId)", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByCustomerId(Long customerId);
}
