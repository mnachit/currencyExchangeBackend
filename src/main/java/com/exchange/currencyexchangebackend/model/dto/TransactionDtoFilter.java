package com.exchange.currencyexchangebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionDtoFilter {
    public String days;
    public String weeks;
    public String months;
    private Long count;
    private String WeekLabel;
}
