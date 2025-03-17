package com.exchange.currencyexchangebackend.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
//@Builder
public class TransactionStatistic {
    private BigDecimal totalExchanges;
    private BigDecimal availableFunds;
    private BigDecimal activeLoans;
    private BigDecimal todayProfit;
    private BigDecimal fundsTrend;
    private BigDecimal exchangesTrend;
    private BigDecimal loansTrend;
    private BigDecimal profitTrend;
}
