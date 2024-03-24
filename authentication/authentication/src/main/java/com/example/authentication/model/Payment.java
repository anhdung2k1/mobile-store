package com.example.authentication.model;

import com.example.authentication.entity.CustomerEntity;
import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long paymentId;
    private MobileEntity mobiles;
    private Set<CustomerEntity> payedCustomers;
    private Date paymentDate;
    private String paymentDescription;
    private String paymentMethod; // Store payment methods: Cash, Credit Card, etc
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
