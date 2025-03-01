package com.exchange.currencyexchangebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String flagUrl;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private LocalDateTime lastUpdated;
}
