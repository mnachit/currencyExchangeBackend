package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    public TransactionDto saveTransaction(TransactionDto transactionDto) throws ValidationException;
}
