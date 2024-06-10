package com.example.authentication.model;

import com.example.authentication.entity.TransactionEntity;
import com.example.authentication.entity.UserEntity;

import java.time.LocalDateTime;

public class Order {
    private Long orderID;
    private UserEntity user;
    private TransactionEntity transaction;
    private String orderDate;
    private String orderStatus;
    private Double totalAmount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
