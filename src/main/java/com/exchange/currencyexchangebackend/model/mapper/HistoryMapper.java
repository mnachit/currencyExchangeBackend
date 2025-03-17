package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.entity.History;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.enums.TypeHistory;
import lombok.Builder;

import java.util.Date;

@Builder
public class HistoryMapper {

    public static History toHistory(Transaction t) {
        History history = new History();
        history.setCustomerName(t.getCustomerName());
        history.setFromCurrency(t.getFromCurrency());
        history.setToCurrency(t.getToCurrency());
        history.setDateDeleted(t.getCreatedAt());
        history.setDateCreated(new Date());
        history.setCompany(t.getCompany());
        history.setTypeHistory(TypeHistory.TRANSACTION);
        return history;
    }
}
