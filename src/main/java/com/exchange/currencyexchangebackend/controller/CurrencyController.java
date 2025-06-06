package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.model.entity.Currency;
import com.exchange.currencyexchangebackend.service.CurrencyService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currency")
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @PostMapping("/save")
    public ResponseEntity<?> saveCurrency(@RequestBody @Valid List<Currency> currency) {
        Response response = new Response<>();
        response.setMessage("Currency created successfully");
        response.setStatus(200);
        response.setResult(currencyService.SaveListCurrency(currency));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getList/{userId}")
    public ResponseEntity<?> getCurrencyList() {
        Response response = new Response<>();
        response.setMessage("Currency list");
        response.setStatus(200);
        response.setResult(currencyService.getAllCurrencies());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCurrency/{code}")
    public ResponseEntity<?> getCurrency(@PathVariable String code) {
        Response response = new Response<>();
        response.setMessage("Currency retrieved successfully");
        response.setStatus(200);
        response.setResult(currencyService.getCurrencyByCode(code));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCurrencyAndBuyRateAndSellRate")
    public ResponseEntity<?> getCurrencyAndBuyRateAndSellRate(@RequestParam String code) {
        Response response = new Response<>();
        response.setMessage("Currency, Buy Rate, and Sell Rate retrieved successfully");
        response.setStatus(200);
        response.setResult(currencyService.getCurrencyAndBuyRateAndSellRate(code));
        return ResponseEntity.ok(response);
    }
}
