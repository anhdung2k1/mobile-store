package com.example.authentication.model;

import com.example.authentication.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {
    private Long transactionId;
    private String transactionType;
    private String shippingAddress;
    private Double billingPayment;
    private PaymentEntity payment;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
