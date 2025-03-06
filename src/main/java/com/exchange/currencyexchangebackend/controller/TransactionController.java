package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/save")
    public ResponseEntity<?> saveTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        Response response = new Response<>();
        response.setMessage("Transaction created successfully");
        response.setStatus(200);
        response.setResult(transactionService.saveTransaction(transactionDto));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getList")
    public ResponseEntity<?> getTransactionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String currency) {

        Response response = new Response<>();
        Pageable pageable = PageRequest.of(page, size);

        // Check if any filters are applied
        if (searchTerm != null || status != null || date != null || currency != null) {
            response.setResult(transactionService.getFilteredTransactions(searchTerm, status, date, currency, pageable));
        } else {
            response.setResult(transactionService.getPaginatedTransactions(pageable));
        }

        response.setMessage("Transaction list");
        response.setStatus(200);
        return ResponseEntity.ok(response);
    }
}