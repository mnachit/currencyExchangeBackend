package com.exchange.currencyexchangebackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Validated
@Builder
public class TransactionDto {
//    @NotBlank(message = "transactionId is mandatory")
//    private String transactionId;
//    @NotBlank(message = "customerName is mandatory")
    private String customerName;
//    @NotBlank(message = "customerId is mandatory")
    private String customerId;
//    @NotBlank(message = "idNumber is mandatory")
    private String idNumber;
//    @NotBlank(message = "phoneNumber is mandatory")
    private String phoneNumber;
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
//    @Positive(message = "dealAmount must be greater than 0")
//    @NotNull(message = "exchangeRate is mandatory")
    private BigDecimal exchangeRate;
//    @Positive(message = "dealAmount must be greater than 0")
//    @NotNull(message = "transactionTimestamp is mandatory")
    private BigDecimal commissionFee;
//    @Positive(message = "dealAmount must be greater than 0")
//    @NotNull(message = "commissionPercentage is mandatory")
    private BigDecimal commissionPercentage;
    @NotNull(message = "Total to Pay is mandatory")
    private BigDecimal totalToPay;
}
