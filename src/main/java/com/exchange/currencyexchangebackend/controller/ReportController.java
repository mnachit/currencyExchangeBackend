package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.service.*;
import com.exchange.currencyexchangebackend.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {
    private final TransactionService transactionService;
    private final FundBalanceService fundBalanceService;
    private final LoanService loanService;
    private final UserService userService;
    private final RecentReportsService recentReportsService;

    @GetMapping("/transactions")
    private ResponseEntity<?> generateTransactionReport(
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token)  {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        try {
            // Convertir les chaînes de dates en objets Date
            Date parsedStartDate = null;
            Date parsedEndDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (startDate != null && !startDate.isEmpty()) {
                parsedStartDate = dateFormat.parse(startDate);
            }

            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Devise invalide: " + currency);
                }
            }

            if (endDate != null && !endDate.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(endDate));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                parsedEndDate = cal.getTime();
            }

            // Gérer la valeur null ou "all" pour currency
            Currency currencyEnum = null;
            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    currencyEnum = Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    // Ignorer si la devise n'est pas valide
                }
            }

//            ReportsDto reportsDto = ReportsDto.builder()
//                    .format(format)
//                    .startDate(parsedStartDate)
//                    .endDate(parsedEndDate)
//                    .currency(currencyEnum)
//                    .status(status)
//                    .reportType("transactions")
//                    .build();
            ReportsDto reportsDto = new ReportsDto();
            reportsDto.setFormat(format);
            reportsDto.setStartDate(parsedStartDate);
            reportsDto.setEndDate(parsedEndDate);
            reportsDto.setCurrency(currencyEnum);
            reportsDto.setStatus(status);
            reportsDto.setReportType("transactions");

            return transactionService.generateTransactionReport(reportsDto, company);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Format de date invalide: " + e.getMessage());
        }
    }

    @GetMapping("/funds")
    private ResponseEntity<?> generateFundsReport(

            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String format,
            @RequestHeader("Authorization") String token)  {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        try {
            // Convertir les chaînes de dates en objets Date
            Date parsedStartDate = null;
            Date parsedEndDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (startDate != null && !startDate.isEmpty()) {
                parsedStartDate = dateFormat.parse(startDate);
            }

            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Devise invalide: " + currency);
                }
            }

            if (endDate != null && !endDate.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(endDate));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                parsedEndDate = cal.getTime();
            }

            // Gérer la valeur null ou "all" pour currency
            Currency currencyEnum = null;
            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    currencyEnum = Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    // Ignorer si la devise n'est pas valide
                }
            }

//            ReportsDto reportsDto = ReportsDto.builder()
//                    .format(format)
//                    .startDate(parsedStartDate)
//                    .endDate(parsedEndDate)
//                    .currency(currencyEnum)
//                    .reportType("funds")
//                    .build();

            ReportsDto reportsDto = new ReportsDto();
            reportsDto.setFormat(format);
            reportsDto.setStartDate(parsedStartDate);
            reportsDto.setEndDate(parsedEndDate);
            reportsDto.setCurrency(currencyEnum);
            reportsDto.setReportType("funds");

            return fundBalanceService.generateFundsReport(reportsDto, company);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Format de date invalide: " + e.getMessage());
        }
    }
//
    @GetMapping("/loans")
    private ResponseEntity<?> generateLoansReport(

            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token)  {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        try {
            // Convertir les chaînes de dates en objets Date
            Date parsedStartDate = null;
            Date parsedEndDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (startDate != null && !startDate.isEmpty()) {
                parsedStartDate = dateFormat.parse(startDate);
            }

            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Devise invalide: " + currency);
                }
            }

            if (endDate != null && !endDate.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(endDate));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                parsedEndDate = cal.getTime();
            }

            // Gérer la valeur null ou "all" pour currency
            Currency currencyEnum = null;
            if (currency != null && !currency.isEmpty() && !currency.equalsIgnoreCase("all")) {
                try {
                    currencyEnum = Currency.valueOf(currency);
                } catch (IllegalArgumentException e) {
                    // Ignorer si la devise n'est pas valide
                }
            }

//            ReportsDto reportsDto = ReportsDto.builder()
//                    .format(format)
//                    .startDate(parsedStartDate)
//                    .endDate(parsedEndDate)
//                    .currency(currencyEnum)
//                    .status(status)
//                    .reportType("loans")
//                    .build();


            ReportsDto reportsDto = new ReportsDto();
            reportsDto.setFormat(format);
            reportsDto.setStartDate(parsedStartDate);
            reportsDto.setEndDate(parsedEndDate);
            reportsDto.setCurrency(currencyEnum);
            reportsDto.setStatus(status);
            reportsDto.setReportType("loans");

            return loanService.generateLoanReport(reportsDto, company);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Format de date invalide: " + e.getMessage());
        }
    }

    @GetMapping("/recentReports")
    private ResponseEntity<?> getRecentReports(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Recent reports");
        response.setStatus(200);
        response.setResult(recentReportsService.getRecent5Reports(company));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recentActivities/{kind}")
    private ResponseEntity<?> recentReportsWithType(@RequestHeader("Authorization") String token, @PathVariable String kind) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response response = new Response<>();
        response.setMessage("Recent reports");
        response.setStatus(200);
        response.setResult(recentReportsService.recentReportsWithType(company, kind));
        return ResponseEntity.ok(response);
    }
}
