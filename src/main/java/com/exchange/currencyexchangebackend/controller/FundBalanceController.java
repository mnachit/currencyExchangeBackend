package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
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

    @PostMapping("save")
    public ResponseEntity<?> saveFundBalance(@RequestBody @Valid FundBalanceDto fundBalanceDto) {
        Response response = new Response<>();
        response.setMessage("Fund balance created successfully");
        response.setResult(fundBalanceService.saveFundBalance(fundBalanceDto));
        return ResponseEntity.ok(response);
    }

    @GetMapping("getList")
    public ResponseEntity<?> getFundBalanceList() {
        Response response = new Response<>();
        response.setMessage("Fund balance list");
        response.setResult(fundBalanceService.getFundBalanceList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("getAvailableBalanceWithCurrency")
    public ResponseEntity<?> getAvailableBalanceWithCurrency(@RequestBody FundBalanceDto fundBalanceDto) {
        Response response = new Response<>();
        response.setMessage("Available balance with currency");
        response.setResult(fundBalanceService.getAvailableBalanceWithCurrency(fundBalanceDto.getUpdatedById(), fundBalanceDto.getCurrency()));
        return ResponseEntity.ok(response);
    }

}
