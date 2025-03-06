package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    public TransactionDto saveTransaction(TransactionDto transactionDto) throws ValidationException;
    public List<TransactionDto> getTransactionList();
    // New paginated methods
    Page<TransactionDto> getPaginatedTransactions(Pageable pageable);

    Page<TransactionDto> getFilteredTransactions(String searchTerm, String status, String date, String currency, Pageable pageable);

    public TransactionStatistic getTransactionStatistics();
}
