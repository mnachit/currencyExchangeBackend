package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface FundBalanceService {
    public FundBalanceDto saveFundBalance(FundBalanceDto fundBalanceDto);
    public List<FundBalanceDto> getFundBalanceList();
    //getAvailableBalanceWithCurrency
    public BigDecimal getAvailableBalanceWithCurrency(Long userId, Currency currencyId);

}
