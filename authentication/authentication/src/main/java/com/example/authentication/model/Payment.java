package com.example.authentication.model;

import com.example.authentication.entity.CustomerEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long paymentId;
    private Set<CustomerEntity> payedCustomers;
    private String paymentDescription;
    private String paymentMethod; // Store payment methods: Cash, Credit Card, etc
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
