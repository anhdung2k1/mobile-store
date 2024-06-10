package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "WISHLIST")
public class WishListEntity {
    public WishListEntity() {
        this.mobiles = null;
        this.user = null;
    }
    public WishListEntity(UserEntity userEntity) {
        this.mobiles = null;
        this.user = userEntity;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WISHLIST_ID", nullable = false, unique = true)
    @TableGenerator(name = "WISHLIST_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "WISHLIST_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long wishListId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "WISHLIST_ID")
    private List<MobileEntity> mobiles;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;
}
