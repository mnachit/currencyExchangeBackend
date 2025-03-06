package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class dashboardController {
    private final TransactionService transactionService;


    @GetMapping("/getStatistics")
    public ResponseEntity<?> getTransactionStatistics() {
        Response response = new Response<>();
        response.setMessage("Transaction statistics");
        response.setStatus(200);
        response.setResult(transactionService.getTransactionStatistics());
        return ResponseEntity.ok(response);
    }
}
