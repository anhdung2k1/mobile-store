package com.example.authentication.service.implement;

import com.example.authentication.entity.TransactionEntity;
import com.example.authentication.model.Transactions;
import com.example.authentication.repository.TransactionRepository;
import com.example.authentication.service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    private Map<String, Object> transactionMap(TransactionEntity transactionEntity) {
        return new HashMap<>(){{
            put("transactionName", transactionEntity.getTransactionName());
            put("transactionType", transactionEntity.getTransactionType());
            put("payment", transactionEntity.getPayments());
        }};
    }

    @Override
    public Transactions createTransaction(Transactions transactions) throws Exception {
        try {
            TransactionEntity transactionEntity = new TransactionEntity();
            BeanUtils.copyProperties(transactions, transactionEntity);
            transactionRepository.save(transactionEntity);
            return transactions;
        } catch (Exception e) {
            throw new Exception("Could not create new transaction" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionByPaymentId(Long paymentId) throws Exception {
        try {
            List<Map<String, Object>> transactionMapList = new ArrayList<>();
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByPaymentId(paymentId).isPresent()
                    ? transactionRepository.findAllTransactionByPaymentId(paymentId).get() : null;
            assert transactionEntities != null;
            transactionEntities.forEach((transactionEntity
                    -> transactionMapList.add(transactionMap(transactionEntity))));
            return transactionMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all the transaction by the payment ID: " + paymentId +  e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionByCustomerId(Long customerId) throws Exception {
        try {
            List<Map<String, Object>> transactionMapList = new ArrayList<>();
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByCustomerId(customerId).isPresent()
                    ? transactionRepository.findAllTransactionByCustomerId(customerId).get() : null;
            assert transactionEntities != null;
            transactionEntities.forEach((transactionEntity
                    -> transactionMapList.add(transactionMap(transactionEntity))));
            return transactionMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all the transaction by the Customer ID: " + customerId +  e.getMessage());
        }
    }

    @Override
    public Boolean deleteTransaction(Long transactionId) throws Exception {
        try {
            if(transactionRepository.findById(transactionId).isPresent()) {
                transactionRepository.delete(transactionRepository.findById(transactionId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found transaction with transaction ID: " + transactionId + e.getMessage());
        }
    }
}
