package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
    private String month;
    private String year;
    private String day;
    private String toCurrency;
    private BigDecimal toAmount;
    private BigDecimal exchangeRate;
    private BigDecimal commissionFee;
    private BigDecimal commissionPercentage;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private String notes;
    private String date;
    private BigDecimal totalPaid;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;
}
