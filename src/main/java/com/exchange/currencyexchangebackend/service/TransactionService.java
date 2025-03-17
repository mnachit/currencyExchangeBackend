package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionDtoFilter;
import com.exchange.currencyexchangebackend.model.dto.TransactionStatistic;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import jakarta.annotation.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    public TransactionDto saveTransaction(TransactionDto transactionDto, Company company, User user) throws ValidationException;
    public List<TransactionDto> getTransactionList();
    // New paginated methods
    Page<TransactionDto> getPaginatedTransactions(Pageable pageable, Company company);

    Page<TransactionDto> getFilteredTransactions(String searchTerm, String status, String date, String currency, Pageable pageable,Company company );

    public TransactionStatistic getTransactionStatistics(Company company);
    public List<TransactionDto> getRecentTransactions(Company company);
    public int getRecentCountTransactionsWithDay(String day, Company company);
    public List<TransactionDtoFilter> getRecentTransactionsLast3Months(Company company);
    public List<TransactionDtoFilter> getRecentTransactionsLast7Days(Company company);
    public List<TransactionDtoFilter> getRecentTransactionsLast4Weeks(Company company);
    public List<TransactionDtoFilter> getRecentTransactionsLast12Months(Company company);
    //deleteTransactions
    public boolean deleteTransactions(List<Long> id) throws ValidationException;
    public ResponseEntity<ByteArrayResource> exportExcel(List<Long> ids) throws ValidationException;
    public ResponseEntity<ByteArrayResource> generateTransactionReport(ReportsDto reportsDto, Company company);
}
