package com.example.authentication.repository;

import com.example.authentication.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query(value =  "SELECT trans.* " +
                    "FROM transactions trans JOIN " +
                    "customer_payment pu ON trans.pay_id = pu.pay_id " +
                    "WHERE pu.cus_id =:customerId " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByCustomerId(Long customerId);

    @Query(value =  "SELECT trans.* FROM transactions trans " +
                    "WHERE trans.trans_name LIKE %:transactionName% " +
                    "LIMIT 28", nativeQuery = true)
    Optional<List<TransactionEntity>> findAllTransactionByName(String transactionName);
}
