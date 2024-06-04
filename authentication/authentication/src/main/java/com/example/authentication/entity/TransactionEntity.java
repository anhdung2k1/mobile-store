package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TRANSACTIONS")
@Transactional(rollbackOn = Exception.class)
public class TransactionEntity {
    public TransactionEntity(){
        this.transactionType = "";
        this.payment = new PaymentEntity();
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public TransactionEntity(String transactionName, String transactionType) {
        this.transactionType = transactionType;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANS_ID", nullable = false, unique = true)
    @TableGenerator(name = "TRANS_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "TRANS_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long transactionId;

    @Column(name = "TRANS_TYPE", nullable = false, length = 100)
    @NotBlank(message = "Must specify the transaction type")
    private String transactionType;

    @Column(name = "SHIPPING_ADDRESS", nullable = false)
    @NotBlank(message = "The Shipping Address must not be blank")
    private String shippingAddress;

    @Column(name = "BILLING_PAYMENT", nullable = false)
    private Double billingPayment;

    @ManyToOne
    @JoinColumn(name = "PAY_ID")
    private PaymentEntity payment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
