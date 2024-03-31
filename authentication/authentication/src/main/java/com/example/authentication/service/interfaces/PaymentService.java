package com.example.authentication.service.interfaces;

import com.example.authentication.model.Payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Payment createPayment(Payment payment) throws Exception;
    //This show the history that user use to pay
    List<Map<String, Object>> getAllPaymentByCustomerId(Long customerId) throws Exception;
    List<Map<String, Object>> getAllPaymentByMobileId(Long mobileId) throws Exception;
    Map<String, Object> getPaymentById(Long paymentId) throws Exception;
    Payment updatePayment(Long paymentId, Payment payment) throws Exception;
    Boolean deletePayment(Long paymentId) throws Exception;
}
