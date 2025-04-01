package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.FundBalance;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FundBalanceRepository extends JpaRepository<FundBalance, Long>, JpaSpecificationExecutor<FundBalance> {
    @Query("select sum(f.amount) from FundBalance f where f.currency = :currency and f.createBy = :user and f.operationFunds = :operationFunds and f.company = :company")
    BigDecimal getAvailableBalanceWithCurrencyAndOperationFunds(@Param("user") User user, @Param("currency") Currency currency, @Param("operationFunds") OperationFunds operationFunds, @Param("company") Company company);//findAllOrderByCreatedAtDesc
    List<FundBalance> findTop10ByCompanyOrderByCreatedAtDesc(@Param("company") Company company);
}
