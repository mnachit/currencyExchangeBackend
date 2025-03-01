package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import com.exchange.currencyexchangebackend.repository.LoanRepository;
import com.exchange.currencyexchangebackend.service.LoanService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;

    @Override
    public LoanDto saveLoan(LoanDto loan) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        return null;
    }
}
