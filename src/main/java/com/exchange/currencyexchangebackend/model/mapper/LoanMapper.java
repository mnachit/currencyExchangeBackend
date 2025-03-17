package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import com.exchange.currencyexchangebackend.model.entity.Loan;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class LoanMapper {

    public static LoanDto toLoanDto(Loan loan) {
        return LoanDto.builder()
                .customerName(loan.getCustomerName())
                .customerId(loan.getId().toString())
                .amount(loan.getAmount())
                .currency(loan.getCurrency())
                .issueDate(loan.getIssueDate())
                .dueDate(loan.getDueDate())
                .status(loan.getStatus())
                .notes(loan.getNotes())
                .interestRate(loan.getInterestRate())
                .company(loan.getCompany())
                .collateral(loan.getCollateral())
                .createdBy(loan.getCreatedBy())
                .isConfidential(loan.getIsConfidential())
                .build();
    }

    public static Loan toLoan(LoanDto loanDto) {
        Loan loan = new Loan();
        loan.setCustomerName(loanDto.getCustomerName());
        loan.setCustomerId(loanDto.getCustomerId());
        loan.setAmount(loanDto.getAmount());
        loan.setCurrency(loanDto.getCurrency());
        loan.setIssueDate(loanDto.getIssueDate());
        loan.setDueDate(loanDto.getDueDate());
        loan.setStatus(loanDto.getStatus());
        loan.setNotes(loanDto.getNotes());
        loan.setInterestRate(loanDto.getInterestRate());
        loan.setCompany(loanDto.getCompany());
        loan.setCollateral(loanDto.getCollateral());
        loan.setIsConfidential(loanDto.getIsConfidential());
        loan.setCreatedBy(loanDto.getCreatedBy());
        return loan;
    }

    public static List<LoanDto> toLoanDtoList(List<Loan> loanList) {
        List<LoanDto> loanDtoList = new ArrayList<>();
        for (Loan loan : loanList) {
            loanDtoList.add(toLoanDto(loan));
        }
        return loanDtoList;
    }

    public static List<Loan> toLoanList(List<LoanDto> loanDtoList) {
        List<Loan> loanList = new ArrayList<>();
        for (LoanDto loanDto : loanDtoList) {
            loanList.add(toLoan(loanDto));
        }
        return loanList;
    }
}
