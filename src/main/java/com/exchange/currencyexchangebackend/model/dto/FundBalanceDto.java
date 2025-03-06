package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Validated
@Builder
public class FundBalanceDto {
    @Enumerated(EnumType.STRING)
    private OperationFunds operationFunds;
    private String code;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal amount;
    private String notes;
    private User updateBy;
    private Date createdAt;
    private Date updatedAt;
    private Long updatedById;
}
