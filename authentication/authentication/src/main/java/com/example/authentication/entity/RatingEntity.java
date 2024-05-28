package com.example.authentication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RATING")
@Transactional(rollbackOn = Exception.class)
public class RatingEntity {
    public RatingEntity() {
        this.count = 0;
        this.rate = 0.0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RATING_ID", nullable = false, unique = true)
    @TableGenerator(name = "RATING_GEN",
            table = "SEQUENCER",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "RATING_SEQ_NEXT_VAL",
            allocationSize = 1)
    private Long ratingId;

    @Column(name = "RATING_COUNT", nullable = false)
    private Integer count;

    @Column(name = "RATING_RATE", nullable = false)
    private Double rate;

    @JsonIgnore
    @OneToOne(mappedBy = "rate", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private MobileEntity mobile;
}
