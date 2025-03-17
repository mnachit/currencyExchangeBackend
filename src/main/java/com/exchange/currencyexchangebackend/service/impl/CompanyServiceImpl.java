package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.CompanyDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.NamePermission;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import com.exchange.currencyexchangebackend.model.mapper.CompanyMapper;
import com.exchange.currencyexchangebackend.repository.CompanyRepository;
import com.exchange.currencyexchangebackend.service.CompanyService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Override
    public CompanyDto saveCompany(CompanyDto companyDto) {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (userService.isEmailExist(companyDto.getEmailManager()))
            errorMessages.add(ErrorMessage.builder().message("Email already exists").build());
        if (userService.isEmailExist(companyDto.getEmail()))
            errorMessages.add(ErrorMessage.builder().message("Company already exists").build());
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        User user = new User();
        user.setFullName(companyDto.getNameManager());
        user.setEmail(companyDto.getEmailManager());
        user.setPassword(companyDto.getPasswordManager());
        userService.saveUser(user, List.of(NamePermission.ALL), RoleUser.MANAGER, companyRepository.save(CompanyMapper.toCompany(companyDto)).getId());
        return companyDto;
    }

    @Override
    public Company getCompanyByUserId(Long userId) {
        return null;
    }
}
