package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.mapper.TransactionMapper;
import com.exchange.currencyexchangebackend.repository.TransactionRepository;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (transactionDto.getCustomerName().isEmpty())
            transactionDto.setCustomerName("Utilisateur"+System.currentTimeMillis());
        return TransactionMapper.toTransactionDto(transactionRepository.save(TransactionMapper.toTransaction(transactionDto)));
    }
}
