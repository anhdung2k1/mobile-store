package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ORDERS")
@Transactional(rollbackOn = Exception.class)
public class OrderEntity {
    public OrderEntity() {
        this.user = new UserEntity();
        this.transaction = new TransactionEntity();
        this.orderDate = "2000-01-01";
        this.orderStatus = "In Delivery";
        this.totalAmount = 0.0;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID", nullable = false, unique = true)
    @TableGenerator(name = "ORDER_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "ORDER_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TRANS_ID", nullable = false)
    private TransactionEntity transaction;

    @Column(name = "ORDER_DATE")
    private String orderDate;

    @Column(name = "ORDER_STATUS")
    private String orderStatus;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;
}
