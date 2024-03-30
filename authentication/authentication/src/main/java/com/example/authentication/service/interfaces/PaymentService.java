package com.example.authentication.service.interfaces;

import com.example.authentication.model.Payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Payment createPayment(Payment payment) throws Exception;
    // Get The History payment
    List<Map<String, Object>> getAllPaymentByUserId(Long userId) throws Exception;

}
