package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "PAYMENT")
@Transactional(rollbackOn = Exception.class)
public class PaymentEntity {
    public PaymentEntity() {
        this.paymentMethod = "";
        this.paymentDescription = "";
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAY_ID", nullable = false, unique = true)
    @TableGenerator(name = "PAY_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "PAY_COUNT",
            pkColumnValue = "PAY_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long paymentId;

    @ManyToMany
    @JoinTable(
            name = "USER_PAYMENT",
            joinColumns = @JoinColumn(name = "PAY_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    Set<UserEntity> payedUsers;

    @Column(name = "PAY_DESC")
    private String paymentDescription;

    @Column(name = "PAY_METHOD", nullable = false)
    @NotBlank(message = "Must specify the payment method")
    private String paymentMethod;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
