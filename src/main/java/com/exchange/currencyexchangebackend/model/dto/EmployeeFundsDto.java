package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeFundsDto {
    private Long id;
    private Long idUser;
    private OperationFunds operationFunds;
    private String fullName;
    private BigDecimal amount;
    private Currency currency;
    private String notes;
}
