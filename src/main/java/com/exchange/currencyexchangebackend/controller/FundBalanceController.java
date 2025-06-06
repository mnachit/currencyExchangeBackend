package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.EmployeeFundsDto;
import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fundBalance")
@AllArgsConstructor
public class FundBalanceController {
    private final FundBalanceService fundBalanceService;
    private final UserService userService;

    @PostMapping("save")
    public ResponseEntity<?> saveFundBalance(@RequestBody @Valid FundBalanceDto fundBalanceDto, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Fund balance created successfully");
        response.setResult(fundBalanceService.saveFundBalance(fundBalanceDto, company, idUser));
        return ResponseEntity.ok(response);
    }

    @GetMapping("getList")
    public ResponseEntity<?> getFundBalanceList(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Fund balance list");
        response.setResult(fundBalanceService.getFundBalanceList(company, idUser));
        return ResponseEntity.ok(response);
    }

    @PostMapping("getAvailableBalanceWithCurrency")
    public ResponseEntity<?> getAvailableBalanceWithCurrency(@RequestBody FundBalanceDto fundBalanceDto, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Available balance with currency");
        response.setResult(fundBalanceService.getAvailableBalanceWithCurrency(idUser, fundBalanceDto.getCurrency(), company));
        return ResponseEntity.ok(response);
    }

    @PostMapping("funds/employees/save")
    public ResponseEntity<?> saveEmployeeFunds(@RequestBody EmployeeFundsDto fundBalanceDto, @RequestHeader("Authorization") String token) {
        System.out.println("saveEmployeeFunds");
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Employee funds saved successfully");
        response.setResult(fundBalanceService.saveEmployeeFunds(fundBalanceDto, company, idUser));
        return ResponseEntity.ok(response);
    }

//    Employee Funds List
    @GetMapping("funds/employees/getList")
    public ResponseEntity<?> getEmployeeFundsList(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Employee funds list");
        response.setResult(fundBalanceService.getEmployeeFundsList(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("funds/employees/history/{userId}")
    public ResponseEntity<?> getEmployeeFundsHistory(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Employee funds history");
        response.setResult(fundBalanceService.getEmployeeFundsHistory(userId,company));
        return ResponseEntity.ok(response);
    }

}
