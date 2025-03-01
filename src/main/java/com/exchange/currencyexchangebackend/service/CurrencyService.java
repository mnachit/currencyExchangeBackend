package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.entity.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    List<Currency> getAllCurrencies();
    Optional<Currency> getCurrencyByCode(String code);
    Currency saveCurrency(Currency currency);
    void deleteCurrency(String code);
    void updateCurrencyRates(String code, BigDecimal buyRate, BigDecimal sellRate);
    BigDecimal calculateExchangeRate(String fromCurrency, String toCurrency, BigDecimal amount);
    BigDecimal calculateComission(BigDecimal amount);
    void refreshRates();
}
