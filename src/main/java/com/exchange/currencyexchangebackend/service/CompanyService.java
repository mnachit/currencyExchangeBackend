package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    public CompanyDto saveCompany(CompanyDto companyDto);
    public Company getCompanyByUserId(Long userId);
}
