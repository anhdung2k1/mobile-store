package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "CARTS")
public class CartEntity {
    public CartEntity() {

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID", nullable = false, unique = true)
    @TableGenerator(name = "CART_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "CART_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long cartId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CART_ID")
    private List<MobileEntity> mobiles;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "CART_DATE")
    private Date cartDate;
}
