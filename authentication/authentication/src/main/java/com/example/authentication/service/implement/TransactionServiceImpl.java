package com.example.authentication.service.implement;

import com.example.authentication.entity.OrderEntity;
import com.example.authentication.entity.PaymentEntity;
import com.example.authentication.entity.TransactionEntity;
import com.example.authentication.model.Transactions;
import com.example.authentication.repository.OrderRepository;
import com.example.authentication.repository.PaymentRepository;
import com.example.authentication.repository.TransactionRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private Map<String, Object> transactionMap(TransactionEntity transactionEntity) {
        return new HashMap<>(){{
            put("transactionID", transactionEntity.getTransactionId());
            put("transactionType", transactionEntity.getTransactionType());
            put("shippingAddress", transactionEntity.getShippingAddress());
            put("billingPayment", transactionEntity.getBillingPayment());
            put("paymentMethod", transactionEntity.getPayment().getPaymentMethod());
        }};
    }

    @Override
    public Boolean createTransaction(Transactions transactions, Long userId) throws Exception {
        try {
            TransactionEntity transactionEntity = new TransactionEntity();
            PaymentEntity paymentEntity = paymentRepository.findPaymentEntitiesByPaymentMethod(transactions.getPayment().getPaymentMethod()).isPresent()
                    ? paymentRepository.findPaymentEntitiesByPaymentMethod(transactions.getPayment().getPaymentMethod()).get() : null;
            assert paymentEntity != null;
            BeanUtils.copyProperties(transactions, transactionEntity);
            transactionEntity.setPayment(paymentEntity);
            transactionEntity.setCreateAt(LocalDateTime.now());
            transactionEntity.setUpdateAt(LocalDateTime.now());
            transactionRepository.save(transactionEntity);
            // Insert to Many to many tables
            Long paymentId = paymentRepository.findPaymentEntitiesByPaymentMethod(transactions.getPayment().getPaymentMethod()).isPresent()
                    ? paymentRepository.findPaymentEntitiesByPaymentMethod(transactions.getPayment().getPaymentMethod()).get().getPaymentId() : null;
            Long transactionId = transactionEntity.getTransactionId();
            assert paymentId != null;
            transactionRepository.insertTransactionWithCustomer(paymentId, userId, transactionId);
            // Create order
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setTransaction(transactionRepository.findById(transactionId).get());
            orderEntity.setOrderStatus("Success");
            orderEntity.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            orderEntity.setTotalAmount(transactionEntity.getBillingPayment());
            orderEntity.setUser(userRepository.findById(userId).get());
            orderRepository.save(orderEntity);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not create new transaction" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionsByName(String transactionName) throws Exception {
        try {
            List<Map<String, Object>> transactionMapList = new ArrayList<>();
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByName(transactionName).isPresent()
                    ? transactionRepository.findAllTransactionByName(transactionName).get() : null;
            assert transactionEntities != null;

            transactionEntities.forEach((transactionEntity
                    -> transactionMapList.add(transactionMap(transactionEntity))));
            return transactionMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all the transaction history " +  e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionByUserId(Long userId) throws Exception {
        try {
            List<Map<String, Object>> transactionMapList = new ArrayList<>();
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByUserId(userId).isPresent()
                    ? transactionRepository.findAllTransactionByUserId(userId).get() : null;
            assert transactionEntities != null;
            transactionEntities.forEach((transactionEntity
                    -> transactionMapList.add(transactionMap(transactionEntity))));
            return transactionMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all the transaction by the userId: " + userId +  e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getTransactionByTransactionId(Long transactionId) throws Exception {
        try {
            TransactionEntity transactionEntity = transactionRepository.findById(transactionId).isPresent()
                    ? transactionRepository.findById(transactionId).get() : null;
            assert transactionEntity != null;
            return transactionMap(transactionEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found transaction with transaction ID: " + transactionId + e.getMessage());
        }
    }


    @Override
    public Transactions updateTransaction(Long transactionId, Transactions transactions) throws Exception {
        try {
            TransactionEntity transactionEntity = transactionRepository.findById(transactionId).isPresent()
                    ? transactionRepository.findById(transactionId).get() : null;
            assert transactionEntity != null;
            transactionEntity.setTransactionType(transactions.getTransactionType());
            transactionEntity.setShippingAddress(transactions.getShippingAddress());
            transactionEntity.setBillingPayment(transactions.getBillingPayment());
            transactionEntity.setUpdateAt(LocalDateTime.now());
            transactionRepository.save(transactionEntity);
            return transactions;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not find the specific transaction with transactionID: " + transactionId + e.getMessage());
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
