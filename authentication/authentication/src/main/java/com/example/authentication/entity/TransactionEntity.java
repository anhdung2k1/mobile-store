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
@Table(name = "USERS")
@Transactional(rollbackOn = Exception.class)
public class TransactionEntity {
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

    @Column(name = "TRANS_NAME", nullable = false)
    @NotBlank(message = "Transaction Name must not be blank")
    private String transactionName;

    @Column(name = "TRANS_TYPE", nullable = false)
    @NotBlank(message = "Must specify the transaction type")
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "PAY_ID")
    private PaymentEntity payments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_At")
    private LocalDateTime updateAt;
}
