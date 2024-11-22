package com.example.authentication.model;

import com.example.authentication.entity.RatingEntity;
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
    private Double mobilePrice;
    private String imageUrl;
    private RatingEntity rate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
