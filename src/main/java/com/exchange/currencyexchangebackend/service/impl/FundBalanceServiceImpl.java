package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.FundBalance;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import com.exchange.currencyexchangebackend.model.mapper.FundBalanceMapper;
import com.exchange.currencyexchangebackend.repository.FundBalanceRepository;
import com.exchange.currencyexchangebackend.repository.RecentReportsRepository;
import com.exchange.currencyexchangebackend.repository.UserRepository;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
import com.exchange.currencyexchangebackend.service.RecentReportsService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FundBalanceServiceImpl implements FundBalanceService {
    private final FundBalanceRepository fundBalanceRepository;
    private final UserRepository userRepository;
    private final RecentReportsRepository recentReportsRepository;
    private final RecentReportsService recentReportsService;

    @Override
    public FundBalanceDto saveFundBalance(FundBalanceDto fundBalanceDto, Company company, Long userId) throws ValidationException {
        fundBalanceDto.setCreatedAt(new Date());
        fundBalanceDto.setUpdatedAt(new Date());
        fundBalanceDto.setCompany(company);
        fundBalanceDto.setCode("FUND"+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
        FundBalance fundBalance = FundBalanceMapper.toEntity(fundBalanceDto);
        fundBalance.setCreateBy(userRepository.findById(userId).get());
        return FundBalanceMapper.toDto(fundBalanceRepository.save(fundBalance));
    }

    @Override
    public List<FundBalanceDto> getFundBalanceList(Company company) {
        return FundBalanceMapper.toDtos(fundBalanceRepository.findTop10ByCompanyOrderByCreatedAtDesc(company));
    }

    @Override
    public BigDecimal getAvailableBalanceWithCurrency(Long userId, Currency currency, Company company) {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        Optional<User> user = userRepository.findById(1L);
        if (!user.isPresent()) {
            errorMessages.add(new ErrorMessage("User not found", 404));
        }
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        BigDecimal addAmount = fundBalanceRepository.getAvailableBalanceWithCurrencyAndOperationFunds(user.get(), currency, OperationFunds.add, company);
        BigDecimal withdrawAmount = fundBalanceRepository.getAvailableBalanceWithCurrencyAndOperationFunds(user.get(), currency, OperationFunds.withdraw, company);
        if (addAmount == null) {
            addAmount = BigDecimal.ZERO;
        }
        if (withdrawAmount == null) {
            withdrawAmount = BigDecimal.ZERO;
        }
        return addAmount.subtract(withdrawAmount);
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateFundsReport(ReportsDto reportsDto, Company company) throws ValidationException {
            try {
                Specification<FundBalance> spec = (root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (reportsDto.getStartDate() != null && reportsDto.getEndDate() != null) {
                        predicates.add(criteriaBuilder.between(
                                root.get("createdAt"),
                                reportsDto.getStartDate(),
                                reportsDto.getEndDate()));
                    } else if (reportsDto.getStartDate() != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                reportsDto.getStartDate()));
                    } else if (reportsDto.getEndDate() != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                reportsDto.getEndDate()));
                    }

                    if (reportsDto.getCurrency() != null) {
                        Predicate fromCurrencyPredicate = criteriaBuilder.equal(
                                root.get("currency"),
                                reportsDto.getCurrency().name());
                        Predicate toCurrencyPredicate = criteriaBuilder.equal(
                                root.get("currency"),
                                reportsDto.getCurrency().name());
                        predicates.add(criteriaBuilder.or(fromCurrencyPredicate, toCurrencyPredicate));
                    }

                    if (reportsDto.getStatus() != null && !reportsDto.getStatus().isEmpty()
                            && !reportsDto.getStatus().equalsIgnoreCase("all")) {
                        try {
                            TransactionStatus status = TransactionStatus.valueOf(reportsDto.getStatus().toUpperCase());
                            predicates.add(criteriaBuilder.equal(root.get("status"), status));
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid transaction status in report request: {}", reportsDto.getStatus());
                        }
                    }

                    if (company != null) {
                        predicates.add(criteriaBuilder.equal(root.get("company"), company));
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                };
                Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
                List<FundBalance> fundBalances = fundBalanceRepository.findAll(spec, sort);

                String format = reportsDto.getFormat() != null ? reportsDto.getFormat().toLowerCase() : "excel";

                switch (format) {
                    case "excel":
                        return generateExcelReport(fundBalances, company, reportsDto);
                    case "pdf":
                        return generateExcelReport(fundBalances, company, reportsDto);
                    case "csv":
                        return generateCsvReport(fundBalances, company, reportsDto);
                    default:
                        return generateExcelReport(fundBalances, company, reportsDto);
                }
            } catch (Exception e) {
                log.error("Error generating fundBalance report", e);
                List<ErrorMessage> errorMessages = new ArrayList<>();
                errorMessages.add(ErrorMessage.builder()
                        .status(500)
                        .message("Error generating report: " + e.getMessage())
                        .build());
                throw new ValidationException(errorMessages);
            }
    }

    /**
     * Generate Excel report from FundBalance data
     */
    private ResponseEntity<ByteArrayResource> generateExcelReport(List<FundBalance> fundBalances, Company company, ReportsDto reportsDto) throws IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Funds Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "ID", "Date", "Type d'opération", "Devise", "Montant", "Remarques", "Créé par"
        };

        // Create header styles
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);

        // Add headers
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create styles for data
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));

        // Add data rows
        int rowNum = 1;
        for (FundBalance fundBalance : fundBalances) {
            Row row = sheet.createRow(rowNum++);

            // ID
            row.createCell(0).setCellValue(fundBalance.getId());

            // Date
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(fundBalance.getCreatedAt());
            dateCell.setCellStyle(dateCellStyle);

            // Operation Type
            String operationType = fundBalance.getOperationFunds().toString();
            // Traduire en français si nécessaire
            if (operationType.equals("add")) {
                operationType = "Ajout";
            } else if (operationType.equals("withdraw")) {
                operationType = "Retrait";
            }
            row.createCell(2).setCellValue(operationType);

            // Currency
            row.createCell(3).setCellValue(fundBalance.getCurrency().toString());

            // Amount
            Cell amountCell = row.createCell(4);
            amountCell.setCellValue(fundBalance.getAmount().doubleValue());
            amountCell.setCellStyle(currencyStyle);

            // Notes
            row.createCell(5).setCellValue(fundBalance.getNotes() != null ? fundBalance.getNotes() : "");

            // Created By
            String createdBy = "";
            if (fundBalance.getCreateBy() != null) {
                createdBy = fundBalance.getCreateBy().getFullName();
            }
            row.createCell(6).setCellValue(createdBy);
        }

        // Add summary rows
        rowNum += 2; // Leave a blank row

        // Summary by Currency and Operation
        Row summaryTitleRow = sheet.createRow(rowNum++);
        CellStyle summaryTitleStyle = workbook.createCellStyle();
        Font summaryTitleFont = workbook.createFont();
        summaryTitleFont.setBold(true);
        summaryTitleStyle.setFont(summaryTitleFont);

        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("Récapitulatif par devise et opération :");
        summaryTitleCell.setCellStyle(summaryTitleStyle);

        // Group by currency and operation
        Map<Currency, Map<OperationFunds, BigDecimal>> summaryCurrencyOp = fundBalances.stream()
                .collect(Collectors.groupingBy(
                        FundBalance::getCurrency,
                        Collectors.groupingBy(
                                FundBalance::getOperationFunds,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        FundBalance::getAmount,
                                        BigDecimal::add)
                        )
                ));

        // Add summary headers
        Row summaryHeaderRow = sheet.createRow(rowNum++);
        summaryHeaderRow.createCell(0).setCellValue("Devise");
        summaryHeaderRow.createCell(1).setCellValue("Type d'opération");
        summaryHeaderRow.createCell(2).setCellValue("Montant total");

        // Add summary data
        for (Map.Entry<Currency, Map<OperationFunds, BigDecimal>> currencyEntry : summaryCurrencyOp.entrySet()) {
            Currency currency = currencyEntry.getKey();

            for (Map.Entry<OperationFunds, BigDecimal> operationEntry : currencyEntry.getValue().entrySet()) {
                Row summaryRow = sheet.createRow(rowNum++);

                summaryRow.createCell(0).setCellValue(currency.toString());

                String operationType = operationEntry.getKey().toString();
                // Traduire en français si nécessaire
                if (operationType.equals("add")) {
                    operationType = "Ajout";
                } else if (operationType.equals("withdraw")) {
                    operationType = "Retrait";
                }
                summaryRow.createCell(1).setCellValue(operationType);

                Cell totalAmountCell = summaryRow.createCell(2);
                totalAmountCell.setCellValue(operationEntry.getValue().doubleValue());
                totalAmountCell.setCellStyle(currencyStyle);
            }
        }

        // Calculate the balance by currency
        rowNum += 2; // Leave a blank row

        Row balanceTitleRow = sheet.createRow(rowNum++);
        Cell balanceTitleCell = balanceTitleRow.createCell(0);
        balanceTitleCell.setCellValue("Solde actuel par devise :");
        balanceTitleCell.setCellStyle(summaryTitleStyle);

        // Add balance headers
        Row balanceHeaderRow = sheet.createRow(rowNum++);
        balanceHeaderRow.createCell(0).setCellValue("Devise");
        balanceHeaderRow.createCell(1).setCellValue("Solde");

        // Calculate the balance
        Map<Currency, BigDecimal> balanceByCurrency = new HashMap<>();

        for (FundBalance fundBalance : fundBalances) {
            Currency currency = fundBalance.getCurrency();
            BigDecimal amount = fundBalance.getAmount();

            // Ajuster le montant en fonction du type d'opération
            if (fundBalance.getOperationFunds() == OperationFunds.withdraw) {
                amount = amount.negate();
            }

            balanceByCurrency.merge(currency, amount, BigDecimal::add);
        }

        // Add balance data
        for (Map.Entry<Currency, BigDecimal> entry : balanceByCurrency.entrySet()) {
            Row balanceRow = sheet.createRow(rowNum++);

            balanceRow.createCell(0).setCellValue(entry.getKey().toString());

            Cell balanceAmountCell = balanceRow.createCell(1);
            balanceAmountCell.setCellValue(entry.getValue().doubleValue());
            balanceAmountCell.setCellStyle(currencyStyle);
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Create resource from byte array
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        // Create file name with timestamp
        String fileName = "funds_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Fund Balance Report"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> generateCsvReport(List<FundBalance> fundBalances, Company company, ReportsDto reportsDto) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        // Add header
        csvContent.append("ID,Date,Type d'opération,Devise,Montant,Remarques,Créé par\n");

        // Add data rows
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (FundBalance fundBalance : fundBalances) {
            csvContent.append(fundBalance.getId()).append(",");
            csvContent.append(dateFormat.format(fundBalance.getCreatedAt())).append(",");

            // Operation Type
            String operationType = fundBalance.getOperationFunds().toString();
            // Traduire en français si nécessaire
            if (operationType.equals("add")) {
                operationType = "Ajout";
            } else if (operationType.equals("withdraw")) {
                operationType = "Retrait";
            }
            csvContent.append(escapeSpecialCharacters(operationType)).append(",");

            csvContent.append(fundBalance.getCurrency()).append(",");
            csvContent.append(fundBalance.getAmount()).append(",");

            if (fundBalance.getNotes() != null) {
                csvContent.append(escapeSpecialCharacters(fundBalance.getNotes()));
            }
            csvContent.append(",");

            // Created By
            String createdBy = "";
            if (fundBalance.getCreateBy() != null) {
                createdBy = fundBalance.getCreateBy().getFullName();
            }
            csvContent.append(escapeSpecialCharacters(createdBy));

            csvContent.append("\n");
        }

        // Add summary section
        csvContent.append("\n\nRécapitulatif par devise et opération\n");
        csvContent.append("Devise,Type d'opération,Montant total\n");

        // Group by currency and operation
        Map<Currency, Map<OperationFunds, BigDecimal>> summaryCurrencyOp = fundBalances.stream()
                .collect(Collectors.groupingBy(
                        FundBalance::getCurrency,
                        Collectors.groupingBy(
                                FundBalance::getOperationFunds,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        FundBalance::getAmount,
                                        BigDecimal::add)
                        )
                ));

        // Add summary data
        for (Map.Entry<Currency, Map<OperationFunds, BigDecimal>> currencyEntry : summaryCurrencyOp.entrySet()) {
            Currency currency = currencyEntry.getKey();

            for (Map.Entry<OperationFunds, BigDecimal> operationEntry : currencyEntry.getValue().entrySet()) {
                csvContent.append(currency).append(",");

                String operationType = operationEntry.getKey().toString();
                // Traduire en français si nécessaire
                if (operationType.equals("add")) {
                    operationType = "Ajout";
                } else if (operationType.equals("withdraw")) {
                    operationType = "Retrait";
                }
                csvContent.append(escapeSpecialCharacters(operationType)).append(",");
                csvContent.append(operationEntry.getValue()).append("\n");
            }
        }

        // Add balance section
        csvContent.append("\n\nSolde actuel par devise\n");
        csvContent.append("Devise,Solde\n");

        // Calculate the balance
        Map<Currency, BigDecimal> balanceByCurrency = new HashMap<>();

        for (FundBalance fundBalance : fundBalances) {
            Currency currency = fundBalance.getCurrency();
            BigDecimal amount = fundBalance.getAmount();

            // Ajuster le montant en fonction du type d'opération
            if (fundBalance.getOperationFunds() == OperationFunds.withdraw) {
                amount = amount.negate();
            }

            balanceByCurrency.merge(currency, amount, BigDecimal::add);
        }

        // Add balance data
        for (Map.Entry<Currency, BigDecimal> entry : balanceByCurrency.entrySet()) {
            csvContent.append(entry.getKey()).append(",");
            csvContent.append(entry.getValue()).append("\n");
        }

        // Convert to byte array
        byte[] bytes = csvContent.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        // Create file name with timestamp
        String fileName = "funds_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Fund Balance Report"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * Escape special characters in CSV
     */
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }

        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }

        return escapedData;
    }

}
