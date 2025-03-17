package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.model.entity.Currency;
import com.exchange.currencyexchangebackend.repository.CurrencyRepository;
import com.exchange.currencyexchangebackend.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    @Override
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        if (currencyRepository.findByCode(code).isPresent()) {
            return currencyRepository.findByCode(code);
        }
        return Optional.empty();
    }

    @Override
    public Currency saveCurrency(Currency currency) {
        return null;
    }

    @Override
    public boolean SaveListCurrency(List<Currency> currency) {
        return currencyRepository.saveAll(currency) != null;
    }

    @Override
    public void deleteCurrency(String code) {

    }

    @Override
    public void updateCurrencyRates(String code, BigDecimal buyRate, BigDecimal sellRate) {

    }

    @Override
    public BigDecimal calculateExchangeRate(String fromCurrency, String toCurrency, BigDecimal amount) {
        return null;
    }

    @Override
    public BigDecimal calculateComission(BigDecimal amount) {
        return null;
    }

    @Override
    public void refreshRates() {

    }
}
