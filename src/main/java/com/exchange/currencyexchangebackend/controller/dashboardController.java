package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.TransactionDtoFilter;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.service.CompanyService;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class dashboardController {
    private final TransactionService transactionService;
    private final UserService userService;


    @GetMapping("/getStatistics")
    public ResponseEntity<?> getStatistics(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Transaction statistics");
        response.setStatus(200);
        response.setResult(transactionService.getTransactionStatistics(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentCountTransactionsWithDay/{day}")
    public ResponseEntity<?> getRecentCountTransactionsWithDay(@PathVariable String day, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Recent transactions count with day");
        response.setStatus(200);
        response.setResult(transactionService.getRecentCountTransactionsWithDay(day, company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentTransactionsLast3Months")
    public ResponseEntity<?> getRecentTransactionsLast3Months(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Recent transactions last 3 months");
        response.setStatus(200);
        response.setResult(transactionService.getRecentTransactionsLast3Months(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentTransactionsLast7Days")
    public ResponseEntity<?> getRecentTransactionsLast7Days(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<List<TransactionDtoFilter>> response = new Response<>();
        response.setMessage("Recent transactions for last 7 days");
        response.setStatus(200);
        response.setResult(transactionService.getRecentTransactionsLast7Days(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentTransactionsLast4Weeks")
    public ResponseEntity<?> getRecentTransactionsLast4Weeks(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<List<TransactionDtoFilter>> response = new Response<>();
        response.setMessage("Recent transactions for last 4 weeks");
        response.setStatus(200);
        response.setResult(transactionService.getRecentTransactionsLast4Weeks(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRecentTransactionsLast12Months")
    public ResponseEntity<?> getRecentTransactionsLast12Months(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<List<TransactionDtoFilter>> response = new Response<>();
        response.setMessage("Recent transactions for last 12 months");
        response.setStatus(200);
        response.setResult(transactionService.getRecentTransactionsLast12Months(company));
        return ResponseEntity.ok(response);
    }
}
