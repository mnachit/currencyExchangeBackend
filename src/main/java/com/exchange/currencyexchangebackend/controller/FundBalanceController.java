package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
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
        response.setResult(fundBalanceService.getFundBalanceList(company));
        return ResponseEntity.ok(response);
    }

    @PostMapping("getAvailableBalanceWithCurrency")
    public ResponseEntity<?> getAvailableBalanceWithCurrency(@RequestBody FundBalanceDto fundBalanceDto, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Available balance with currency");
        response.setResult(fundBalanceService.getAvailableBalanceWithCurrency(fundBalanceDto.getUpdatedById(), fundBalanceDto.getCurrency(), company));
        return ResponseEntity.ok(response);
    }

}
