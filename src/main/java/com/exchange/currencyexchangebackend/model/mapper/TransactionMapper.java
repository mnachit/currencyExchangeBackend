package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.entity.Transaction;

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
                .totalToPay(transaction.getTotalToPay())
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
        transaction.setTotalToPay(transactionDto.getTotalToPay());
        return transaction;
    }
}
