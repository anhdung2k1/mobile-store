package com.example.authentication.model;

import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishList {
    private Long wishListId;
    private List<MobileEntity> mobiles;
    private UserEntity user;
}
