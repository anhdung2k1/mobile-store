package com.example.authentication.repository;

import com.example.authentication.entity.TransactionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query(value =  "SELECT trans.* " +
                    "FROM transactions trans INNER JOIN " +
                    "customer_payment cp ON trans.trans_id = cp.trans_id " +
                    "WHERE cp.cus_id =:customerId " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByCustomerId(Long customerId);

    @Query(value =  "SELECT trans.* FROM transactions trans " +
                    "WHERE trans.trans_name LIKE %:transactionName% " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByName(String transactionName);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT INTO customer_payment(pay_id, cus_id, trans_id) VALUES(:paymentId, :customerId, :transactionId)", nativeQuery = true)
    void insertTransactionWithCustomer(Long paymentId, Long customerId, Long transactionId);
}
