package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "CUSTOMERS")
@Transactional(rollbackOn = Exception.class)
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUS_ID", nullable = false, unique = true)
    @TableGenerator(name = "CUS_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "CUS_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long customerId;

    @Column(name = "CUS_NAME", nullable = false)
    @NotBlank(message = "Customer Name must not be blank")
    @Size(min = 3, message = "Customer Name at least 3 characters")
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "MOB_ID")
    private MobileEntity mobile;

    @ManyToMany(mappedBy = "payedCustomers")
    Set<PaymentEntity> pays;

    @Column(name = "CUS_ADDRESS")
    private String customerAddress;

    @Column(name = "CUS_GENDER")
    private String customerGender;

    @Column(name = "CUS_BIRTH_DAY")
    @Temporal(TemporalType.DATE)
    private Date customerBirthDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
