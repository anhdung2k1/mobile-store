package com.example.authentication.model;

import com.example.authentication.entity.UserEntity;
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
    private Set<UserEntity> payedUsers;
    private String paymentDescription;
    private String paymentMethod; // Store payment methods: Cash, Credit Card, etc
    private String imageUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
