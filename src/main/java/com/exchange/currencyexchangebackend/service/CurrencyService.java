package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.entity.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface CurrencyService {
    List<Currency> getAllCurrencies();
    Optional<Currency> getCurrencyByCode(String code);
    Currency saveCurrency(Currency currency);
    boolean SaveListCurrency(List<Currency> currency);
    void deleteCurrency(String code);
    void updateCurrencyRates(String code, BigDecimal buyRate, BigDecimal sellRate);
    BigDecimal calculateExchangeRate(String fromCurrency, String toCurrency, BigDecimal amount);
    BigDecimal calculateComission(BigDecimal amount);
    void refreshRates();
}
