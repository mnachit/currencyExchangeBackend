package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
@Data
@Builder
public class ReportsDto {
    private String format;
    private Date startDate;  // Doit être un Date, pas un String
    private Date endDate;    // Doit être un Date, pas un String
    private Currency currency;
    private String status;
    private String reportType;
}