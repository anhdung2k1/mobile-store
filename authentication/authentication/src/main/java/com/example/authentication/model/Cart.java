package com.example.authentication.model;

import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long cartId;
    private List<MobileEntity> mobile;
    private UserEntity user;
    private Date date;
}
