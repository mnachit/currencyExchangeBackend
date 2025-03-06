package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import com.exchange.currencyexchangebackend.service.CompanyService;
import com.exchange.currencyexchangebackend.util.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/save")
    private ResponseEntity<?> saveCompany(@RequestBody @Valid CompanyDto companyDto) {
        Response response = new Response<>();
        response.setMessage("Company created successfully");
        response.setResult(companyService.saveCompany(companyDto));
        response.setStatus(200);
        return ResponseEntity.ok(response);
    }
}
