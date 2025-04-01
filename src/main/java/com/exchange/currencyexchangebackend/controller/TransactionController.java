package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.ImportResponseDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.Currency;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.service.CompanyService;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<?> saveTransaction(@RequestBody @Valid TransactionDto transactionDto,
                                             @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        User user = userService.getUserById(idUser);
        Response response = new Response<>();
        response.setMessage("Transaction created successfully");
        response.setStatus(200);
        response.setResult(transactionService.saveTransaction(transactionDto, company, user));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getList")
    public ResponseEntity<?> getTransactionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String currency,
            @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);

        Response response = new Response<>();
        Pageable pageable = PageRequest.of(page, size);

        // Check if any filters are applied
        if (searchTerm != null || status != null || date != null || currency != null) {
            response.setResult(transactionService.getFilteredTransactions(searchTerm, status, date, currency, pageable, company));
        } else {
            response.setResult(transactionService.getPaginatedTransactions(pageable, company));
        }

        response.setMessage("Transaction list");
        response.setStatus(200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentTransactions")
    public ResponseEntity<?> getRecentTransactions(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Recent transactions");
        response.setStatus(200);
        response.setResult(transactionService.getRecentTransactions(company));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deleteTransactions")
    public ResponseEntity<?> deleteTransactions(@RequestBody List<Long> transactionIds,
                                                @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        User user = userService.getUserById(idUser);
        Response response = new Response<>();
        response.setMessage("Transactions deleted successfully");
        response.setStatus(200);
        response.setResult(transactionService.deleteTransactions(transactionIds, user, company));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exportExcel")
    public ResponseEntity<ByteArrayResource> exportExcel(@RequestBody List<Long> transactionIds) throws ValidationException {
        return transactionService.exportExcel(transactionIds);
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResponseDto> importTransactions(
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResponseDto(0, "File is empty"));
        }

        try {
            int count = transactionService.importTransactionsFromExcel(file, authentication, company, userService.getUserById(idUser));
            return ResponseEntity.ok(new ImportResponseDto(count, "Successfully imported " + count + " transactions"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResponseDto(0, "Error processing file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ImportResponseDto(0, "Error importing transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/calculateStatistics")
    public ResponseEntity<?> calculateStatistics(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Transaction statistics");
        response.setStatus(200);
        response.setResult(transactionService.getTransactionStatistics(company));
        return ResponseEntity.ok(response);
    }
}