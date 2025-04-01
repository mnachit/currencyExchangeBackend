package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Validated
@Builder
public class LoanDto {
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
    private Company company;
    private String collateral;
    private Boolean isConfidential;
    private Date createdAt;
    private Date updatedAt;
    private User createdBy;
}
