package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String loanId; // L78901 format
    private String customerName;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LoanStatus status;
    private String notes;
    private BigDecimal interestRate;
}
