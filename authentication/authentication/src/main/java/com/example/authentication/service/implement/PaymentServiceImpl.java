package com.example.authentication.service.implement;

import com.example.authentication.entity.PaymentEntity;
import com.example.authentication.model.Payment;
import com.example.authentication.repository.PaymentRepository;
import com.example.authentication.service.interfaces.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    private HashMap<String, Object> paymentMap(PaymentEntity paymentEntity) {
        return new HashMap<>() {{
            put("paymentMethod", paymentEntity.getPaymentMethod());
            put("paymentDate", paymentEntity.getPaymentDate());
            put("paymentDescription", paymentEntity.getPaymentDescription());
            put("mobile", paymentEntity.getMobiles());
        }};
    }
    @Override
    public Payment createPayment(Payment payment) throws Exception {
        try {
            PaymentEntity paymentEntity = new PaymentEntity();
            BeanUtils.copyProperties(payment, paymentEntity);
            return payment;
        } catch (Exception e) {
            throw new Exception("Could not create new Payment " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPaymentByCustomerId(Long customerId) throws Exception {
        try {
            List<Map<String, Object>> paymentMapList = new ArrayList<>();
            List<PaymentEntity> paymentEntities = paymentRepository.findAllPaymentByCustomerId(customerId).isPresent()
                    ? paymentRepository.findAllPaymentByCustomerId(customerId).get() : null;
            assert paymentEntities != null;
            paymentEntities.forEach((paymentEntity ->
                    paymentMapList.add(paymentMap(paymentEntity))));
            return paymentMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get list of payments from " + customerId + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPaymentByMobileId(Long mobileId) throws Exception {
        try {
            List<Map<String, Object>> paymentMapList = new ArrayList<>();
            List<PaymentEntity> paymentEntities = paymentRepository.findAllPaymentByMobileId(mobileId).isPresent()
                    ? paymentRepository.findAllPaymentByMobileId(mobileId).get() : null;
            assert paymentEntities != null;
            paymentEntities.forEach((paymentEntity -> paymentMapList.add(paymentMap(paymentEntity))));
            return paymentMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get list of payments from " + mobileId + e.getMessage());
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
            paymentEntity.setPaymentDate(payment.getPaymentDate());
            paymentEntity.setPaymentDescription(payment.getPaymentDescription());
            paymentEntity.setMobiles(payment.getMobiles());
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
