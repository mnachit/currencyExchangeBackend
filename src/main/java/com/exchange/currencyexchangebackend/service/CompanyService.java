package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    public CompanyDto saveCompany(CompanyDto companyDto);
}
