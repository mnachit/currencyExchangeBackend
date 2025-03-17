package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanService {
    public LoanDto saveLoan(LoanDto loan, Company company, Long idUser) throws ValidationException;

    Page<LoanDto> getPaginatedLoans(Pageable pageable, Company company);
    Page<LoanDto> getLoanList(String searchTerm, String status, String date, String currency, Pageable pageable, Company company);
    public boolean deleteLoans(List<Long> id, Company company) throws ValidationException;
    public boolean changeLoanStatus(Long id, LoanStatus status, Company company) throws ValidationException;
    //generateLoanReport
    public ResponseEntity<ByteArrayResource> generateLoanReport(ReportsDto reportsDto, Company company) throws ValidationException;
}
