package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String transactionId;
    private String customerName;
    private String customerId;
    private String idNumber;
    private String phoneNumber;
    private String fromCurrency;
    private BigDecimal fromAmount;
    private String toCurrency;
    private BigDecimal toAmount;
    private BigDecimal exchangeRate;
    private BigDecimal commissionFee;
    private BigDecimal commissionPercentage;
    private TransactionStatus status;
    private String notes;
    private LocalDateTime date;
    private BigDecimal totalToPay;
}
