package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import com.exchange.currencyexchangebackend.model.entity.Company;

public class CompanyMapper {
    public static CompanyDto toCompanyDto(Company company) {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(company.getName());
        companyDto.setAddress(company.getAddress());
        companyDto.setCity(company.getCity());
        companyDto.setPhone(company.getPhone());
        companyDto.setEmail(company.getEmail());
        companyDto.setWebsite(company.getWebsite());
        return companyDto;
    }

    public static Company toCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setAddress(companyDto.getAddress());
        company.setCity(companyDto.getCity());
        company.setPhone(companyDto.getPhone());
        company.setEmail(companyDto.getEmail());
        company.setWebsite(companyDto.getWebsite());
        return company;
    }
}
