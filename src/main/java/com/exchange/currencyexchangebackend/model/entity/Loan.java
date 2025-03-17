package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private LocalDate issueDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    private String notes;
    private BigDecimal interestRate;
    private String collateral;
    private Date createdAt;
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private Boolean isConfidential;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}
