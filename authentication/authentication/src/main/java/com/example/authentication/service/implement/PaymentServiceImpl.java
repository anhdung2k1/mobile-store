package com.example.authentication.service.implement;

import com.example.authentication.entity.PaymentEntity;
import com.example.authentication.model.Payment;
import com.example.authentication.repository.PaymentRepository;
import com.example.authentication.service.interfaces.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    private HashMap<String, Object> paymentMap(PaymentEntity paymentEntity) {
        return new HashMap<>() {{
            put("paymentID", paymentEntity.getPaymentId());
            put("paymentMethod", paymentEntity.getPaymentMethod());
            put("paymentDescription", paymentEntity.getPaymentDescription());
            put("imageUrl", paymentEntity.getImageUrl() != null ? paymentEntity.getImageUrl() : "");
        }};
    }
    @Override
    public Map<String, Object> createPayment(Payment payment) throws Exception {
        try {
            PaymentEntity paymentEntity;
            if (paymentRepository.findPaymentEntitiesByPaymentMethod(payment.getPaymentMethod()).isPresent()) {
                paymentEntity = paymentRepository.findPaymentEntitiesByPaymentMethod(payment.getPaymentMethod()).get();
                BeanUtils.copyProperties(paymentEntity, payment);
                log.warn("The payment {} method already existed", payment.getPaymentMethod());
            }
            else {
                paymentEntity = new PaymentEntity();
                BeanUtils.copyProperties(payment, paymentEntity);
                paymentRepository.save(paymentEntity);
            }
            return paymentMap(paymentEntity);
        } catch (Exception e) {
            throw new Exception("Could not create new Payment " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPaymentByUserId(Long userId) throws Exception {
        try {
            List<Map<String, Object>> paymentMapList = new ArrayList<>();
            List<PaymentEntity> paymentEntities = paymentRepository.findAllPaymentByUserId(userId).isPresent()
                    ? paymentRepository.findAllPaymentByUserId(userId).get() : null;
            assert paymentEntities != null;
            paymentEntities.forEach((paymentEntity ->
                    paymentMapList.add(paymentMap(paymentEntity))));
            return paymentMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get list of payments from " + userId + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPayments() throws Exception {
        try {
            List<Map<String, Object>> paymentMapList = new ArrayList<>();
            List<PaymentEntity> paymentEntities = paymentRepository.findAll();
            paymentEntities.forEach((paymentEntity -> paymentMapList.add(paymentMap(paymentEntity))));
            return paymentMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get list of payments " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPaymentById(Long paymentId) throws Exception {
        try {
            PaymentEntity paymentEntity = paymentRepository.findById(paymentId).isPresent()
                    ? paymentRepository.findById(paymentId).get() : null;
            assert paymentEntity != null;
            return paymentMap(paymentEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get payment by " + paymentId + e.getMessage());
        }
    }

    @Override
    public Payment updatePayment(Long paymentId, Payment payment) throws Exception {
        try {
            PaymentEntity paymentEntity = paymentRepository.findById(paymentId).isPresent()
                    ? paymentRepository.findById(paymentId).get() : null;
            assert paymentEntity != null;
            paymentEntity.setPaymentMethod(payment.getPaymentMethod());
            paymentEntity.setPaymentDescription(payment.getPaymentDescription());
            paymentEntity.setUpdateAt(LocalDateTime.now());
            paymentRepository.save(paymentEntity);
            BeanUtils.copyProperties(paymentEntity, payment);
            return payment;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get payment by " + paymentId + e.getMessage());
        }
    }

    @Override
    public Boolean deletePayment(Long paymentId) throws Exception {
        try {
            if(paymentRepository.findById(paymentId).isPresent()) {
                paymentRepository.delete(paymentRepository.findById(paymentId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found Payment with paymentID " + paymentId + e.getMessage());
        }
    }
}
