package com.example.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mobile {
    private Long mobileId;
    private String mobileName;
    private String mobileModel;
    private String mobileType;
    private String mobileDescription;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
