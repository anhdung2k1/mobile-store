package com.example.authentication.service.interfaces;

import com.example.authentication.model.Transactions;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    Transactions createTransaction(Transactions transactions) throws Exception;
    List<Map<String, Object>> getAllTransactionByPaymentId(Long paymentId) throws Exception;
    List<Map<String, Object>> getAllTransactionByCustomerId(Long customerId) throws Exception;
    Boolean deleteTransaction(Long transactionId) throws Exception;
}
