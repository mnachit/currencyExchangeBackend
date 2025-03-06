package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.entity.Transaction;

import java.util.List;

public class TransactionMapper {
    public static TransactionDto toTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .customerName(transaction.getCustomerName())
                .customerId(transaction.getCustomerId())
                .idNumber(transaction.getIdNumber())
                .phoneNumber(transaction.getPhoneNumber())
                .fromCurrency(transaction.getFromCurrency())
                .fromAmount(transaction.getFromAmount())
                .toCurrency(transaction.getToCurrency())
                .toAmount(transaction.getToAmount())
                .exchangeRate(transaction.getExchangeRate())
                .commissionFee(transaction.getCommissionFee())
                .commissionPercentage(transaction.getCommissionPercentage())
                .totalPaid(transaction.getTotalPaid())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .month(transaction.getMonth())
                .year(transaction.getYear())
                .date(transaction.getDate())
                .build();
    }

    public static Transaction toTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setCustomerName(transactionDto.getCustomerName());
        transaction.setCustomerId(transactionDto.getCustomerId());
        transaction.setIdNumber(transactionDto.getIdNumber());
        transaction.setPhoneNumber(transactionDto.getPhoneNumber());
        transaction.setFromCurrency(transactionDto.getFromCurrency());
        transaction.setFromAmount(transactionDto.getFromAmount());
        transaction.setToCurrency(transactionDto.getToCurrency());
        transaction.setToAmount(transactionDto.getToAmount());
        transaction.setExchangeRate(transactionDto.getExchangeRate());
        transaction.setCommissionFee(transactionDto.getCommissionFee());
        transaction.setCommissionPercentage(transactionDto.getCommissionPercentage());
        transaction.setTotalPaid(transactionDto.getTotalPaid());
        transaction.setStatus(transactionDto.getStatus());
        transaction.setCreatedAt(transactionDto.getCreatedAt());
        transaction.setUpdatedAt(transactionDto.getUpdatedAt());
        transaction.setMonth(transactionDto.getMonth());
        transaction.setYear(transactionDto.getYear());
        transaction.setDate(transactionDto.getDate());
        return transaction;
    }

    public static List<TransactionDto> toTransactionDtos(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toTransactionDto).toList();
    }

    public static List<Transaction> toTransactions(List<TransactionDto> transactionDtos) {
        return transactionDtos.stream().map(TransactionMapper::toTransaction).toList();
    }
}
