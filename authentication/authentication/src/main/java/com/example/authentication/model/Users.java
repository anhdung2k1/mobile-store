package com.example.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private Long user_id;
    private String userName;
    private Date birth_day;
    private String address;
    private String gender;
    private String imageURL;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
