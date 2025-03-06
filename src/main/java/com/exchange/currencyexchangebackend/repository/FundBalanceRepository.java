package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.FundBalance;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FundBalanceRepository extends JpaRepository<FundBalance, Long> {
    @Query("select sum(f.amount) from FundBalance f where f.currency = :currency and f.updateBy = :user and f.operationFunds = :operationFunds")
    BigDecimal getAvailableBalanceWithCurrencyAndOperationFunds(@Param("user") User user, @Param("currency") Currency currency, @Param("operationFunds") OperationFunds operationFunds);//findAllOrderByCreatedAtDesc
    public List<FundBalance> findAllByOrderByCreatedAtDesc();
}
