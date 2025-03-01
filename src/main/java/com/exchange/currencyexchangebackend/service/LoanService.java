package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {
    public LoanDto saveLoan(LoanDto loan) throws ValidationException;
}
