package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import com.exchange.currencyexchangebackend.model.mapper.CompanyMapper;
import com.exchange.currencyexchangebackend.repository.CompanyRepository;
import com.exchange.currencyexchangebackend.service.CompanyService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    @Override
    public CompanyDto saveCompany(CompanyDto companyDto) {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        return CompanyMapper.toCompanyDto(companyRepository.save(CompanyMapper.toCompany(companyDto)));
    }
}
