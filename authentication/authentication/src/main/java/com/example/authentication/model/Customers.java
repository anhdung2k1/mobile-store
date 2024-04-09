package com.example.authentication.model;

import com.example.authentication.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customers {
    private Long customerID;
    private String customerName;
    private Set<PaymentEntity> pays;
    private String customerEmail;
    private String customerAddress;
    private String customerGender;
    private String customerBirthDay;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
