package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionDto transactionDto) {
        Response response = new Response<>();
        response.setMessage("Transaction created successfully");
        response.setResult(transactionService.saveTransaction(transactionDto));
        return ResponseEntity.ok(response);
    }
}
