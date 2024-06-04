package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "USERS")
@Transactional(rollbackOn = Exception.class)
public class UserEntity implements Serializable{
    //Default Constructor
    public UserEntity(){
        this.address = "UNKNOWN";
        this.gender = "UNKNOWN";
        this.birth_day = new Date();
        this.userName = "UNKNOWN";
        this.imageUrl = "";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    public UserEntity(String userName) {
        this.userName = userName;
        this.address = "UNKNOWN";
        this.gender = "UNKNOWN";
        this.imageUrl = "";
        this.birth_day = new Date();
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false, unique = true)
    @TableGenerator(name = "USER_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "USER_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long user_id;

    @Column(name = "USER_NAME", nullable = false, length = 20)
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "BIRTH_DAY")
    private Date birth_day;

    @Column(name = "ADDRESS", length = 30)
    private String address;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "GENDER", length = 6)
    private String gender;

    @ManyToMany(mappedBy = "payedUsers")
    Set<PaymentEntity> pays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
