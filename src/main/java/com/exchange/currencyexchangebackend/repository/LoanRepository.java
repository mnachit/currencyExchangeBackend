package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.Loan;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    @Query("select sum(t.amount) from Loan t where t.status = :status and t.company = :company")
    BigDecimal countByStatus(LoanStatus status, Company company);
    Page<Loan> findByCompany(Company company, Pageable pageable);
    @Query(value = "SELECT t FROM Loan t WHERE t.company = :company")
    Page<Loan> PaginatedLoanWithCompany(Pageable pageable, Company company);

    Page<Loan> findAll(Specification<Loan> spec, Pageable pageableWithSort);
}
