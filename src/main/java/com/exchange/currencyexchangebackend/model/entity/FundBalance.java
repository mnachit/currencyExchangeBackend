package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "FundBalance")
public class FundBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    @Enumerated(EnumType.STRING)
    private OperationFunds operationFunds;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal amount;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updateBy;
}
