package com.example.authentication.service.interfaces;

import com.example.authentication.model.Payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createPayment(Payment payment) throws Exception;
    //This show the history that user use to pay
    List<Map<String, Object>> getAllPaymentByUserId(Long userId) throws Exception;
    List<Map<String, Object>> getAllPayments() throws Exception;
    Map<String, Object> getPaymentById(Long paymentId) throws Exception;
    Payment updatePayment(Long paymentId, Payment payment) throws Exception;
    Boolean deletePayment(Long paymentId) throws Exception;
}
