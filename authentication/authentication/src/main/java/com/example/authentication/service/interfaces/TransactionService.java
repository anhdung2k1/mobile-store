package com.example.authentication.service.interfaces;

import com.example.authentication.model.Transactions;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    Boolean createTransaction(Transactions transactions) throws Exception;
    List<Map<String, Object>> getAllTransactionsByName(String transactionName) throws Exception;
    List<Map<String, Object>> getAllTransactionByCustomerId(Long customerId) throws Exception;
    Map<String, Object> getTransactionByTransactionId(Long transactionId) throws Exception;
    Transactions updateTransaction(Long transactionId, Transactions transactions) throws Exception;
    Boolean deleteTransaction(Long transactionId) throws Exception;
}
