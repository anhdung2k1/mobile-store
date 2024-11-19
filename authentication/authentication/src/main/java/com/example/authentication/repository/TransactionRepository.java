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
                    "user_payment cp ON trans.trans_id = cp.trans_id " +
                    "WHERE cp.user_id =:userId " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByUserId(Long userId);

    @Query(value =  "SELECT trans.* FROM transactions trans " +
                    "WHERE trans.trans_name LIKE %:transactionName% " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByName(String transactionName);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT INTO user_payment(pay_id, user_id, trans_id) VALUES(:paymentId, :userId, :transactionId)", nativeQuery = true)
    void insertTransactionWithCustomer(Long paymentId, Long userId, Long transactionId);
}
