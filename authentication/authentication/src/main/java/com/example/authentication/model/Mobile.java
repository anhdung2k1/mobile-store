package com.example.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mobile {
    private Long mobileID;
    private String mobileName;
    private String mobileModel;
    private String mobileType;
    private String mobileDescription;
    private Integer mobileQuantity;
    private String mobilePrice;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
