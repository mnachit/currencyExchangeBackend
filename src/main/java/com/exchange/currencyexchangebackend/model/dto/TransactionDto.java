package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Validated
@Builder
public class TransactionDto {
    private Long id;
    private String customerName;
    private String customerId;
    private String idNumber;
    private String phoneNumber;
    private String month;
    private String year;
    private String date;
    private String day;
    @NotBlank(message = "email is mandatory")
    private String fromCurrency;
    @Positive(message = "dealAmount must be greater than 0")
    @NotNull(message = "fromAmount is mandatory")
    private BigDecimal fromAmount;
    @NotBlank(message = "toCurrency is mandatory")
    private String toCurrency;
    @Positive(message = "dealAmount must be greater than 0")
    @NotNull(message = "toAmount is mandatory")
    private BigDecimal toAmount;
    private BigDecimal exchangeRate;
    private BigDecimal commissionFee;
    private BigDecimal commissionPercentage;
    @NotNull(message = "Total to Pay is mandatory")
    private BigDecimal totalPaid;
    private TransactionStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Company company;
    private User createdBy;
    private User updatedBy;
}