package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "MOBILE")
@Transactional(rollbackOn = Exception.class)
public class MobileEntity {
    public MobileEntity() {
        this.mobileName = "";
        this.mobileModel = "";
        this.mobileType = "";
        this.mobileDescription = "";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOB_ID", nullable = false, unique = true)
    @TableGenerator(name = "MOB_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "MOB_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long mobileId;

    @Column(name = "MOB_NAME", nullable = false) //For example: Iphone 15 Pro Max
    @NotBlank(message = "Mobile Name must not be blank")
    private String mobileName;

    @Column(name = "MOB_MODEL", nullable = false) // This will determine what OS phone run. For example: OS, Android
    @NotBlank(message = "Mobile Model Name must not be blank")
    private String mobileModel;

    @Column(name = "MOB_TYPE", nullable = false) // Identify the mobile type. Iphone, Samsung, ROG, etc
    @NotBlank(message = "Mobile Type must not be blank")
    private String mobileType;

    @Column(name = "MOB_DESC")
    private String mobileDescription; // The description for individual mobile

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
