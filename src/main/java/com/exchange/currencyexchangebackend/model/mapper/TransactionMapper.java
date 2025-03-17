package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.entity.Transaction;

import java.util.List;

public class TransactionMapper {
    public static TransactionDto toTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
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
                .day(transaction.getDay())
                .month(transaction.getMonth())
                .year(transaction.getYear())
                .date(transaction.getDate())
                .company(transaction.getCompany())
                .createdBy(transaction.getCreatedBy())
                .updatedBy(transaction.getUpdatedBy())
                .build();
    }

    public static Transaction toTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setId(transactionDto.getId());
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
        transaction.setDay(transactionDto.getDay());
        transaction.setCreatedAt(transactionDto.getCreatedAt());
        transaction.setUpdatedAt(transactionDto.getUpdatedAt());
        transaction.setMonth(transactionDto.getMonth());
        transaction.setYear(transactionDto.getYear());
        transaction.setDate(transactionDto.getDate());
        transaction.setCompany(transactionDto.getCompany());
        transaction.setCreatedBy(transactionDto.getCreatedBy());
        transaction.setUpdatedBy(transactionDto.getUpdatedBy());
        return transaction;
    }

    public static List<TransactionDto> toTransactionDtos(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toTransactionDto).toList();
    }

    public static List<Transaction> toTransactions(List<TransactionDto> transactionDtos) {
        return transactionDtos.stream().map(TransactionMapper::toTransaction).toList();
    }
}
