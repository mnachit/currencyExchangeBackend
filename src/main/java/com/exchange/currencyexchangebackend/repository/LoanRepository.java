package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Loan;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select sum(t.amount) from Loan t where t.status = :status")
    BigDecimal countByStatus(LoanStatus status);
}
