package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import com.exchange.currencyexchangebackend.service.LoanService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@AllArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<?> saveLoan(@RequestBody @Valid LoanDto loanDto, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Loan created successfully");
        response.setResult(loanService.saveLoan(loanDto, company, idUser));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getList")
    public ResponseEntity<Response<Page<LoanDto>>> getLoanList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String currency,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<Page<LoanDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size);

        response.setMessage("Loan list");
        if (searchTerm != null || status != null || date != null || currency != null) {
            response.setResult(loanService.getLoanList(searchTerm, status, date, currency, pageable, company));
        } else {
            response.setResult(loanService.getPaginatedLoans(pageable, company));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable List<Long> id, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Loan deleted successfully");
        response.setResult(loanService.deleteLoans(id, company));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/status/{id}/{status}")
    public ResponseEntity<?> changeLoanStatus(@PathVariable Long id, @RequestHeader("Authorization") String token, @PathVariable String status) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();

        try {
            LoanStatus loanStatus = LoanStatus.valueOf(status);
            response.setMessage("Loan status changed successfully");
            response.setResult(loanService.changeLoanStatus(id, loanStatus, company));
        } catch (IllegalArgumentException e) {
            response.setMessage("Invalid loan status: " + status);
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
