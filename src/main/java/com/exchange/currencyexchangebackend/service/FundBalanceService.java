package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface FundBalanceService {
    public FundBalanceDto saveFundBalance(FundBalanceDto fundBalanceDto, Company company, Long userId) throws ValidationException;
    public List<FundBalanceDto> getFundBalanceList(Company company);
    //getAvailableBalanceWithCurrency
    public BigDecimal getAvailableBalanceWithCurrency(Long userId, Currency currencyId, Company company);
    public ResponseEntity<ByteArrayResource> generateFundsReport(ReportsDto reportsDto, Company company) throws ValidationException;

}
