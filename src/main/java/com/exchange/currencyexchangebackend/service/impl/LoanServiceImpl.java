package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.LoanDto;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.Loan;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import com.exchange.currencyexchangebackend.model.mapper.LoanMapper;
import com.exchange.currencyexchangebackend.model.mapper.TransactionMapper;
import com.exchange.currencyexchangebackend.repository.LoanRepository;
import com.exchange.currencyexchangebackend.repository.RecentReportsRepository;
import com.exchange.currencyexchangebackend.service.LoanService;
import com.exchange.currencyexchangebackend.service.RecentReportsService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final RecentReportsRepository recentReportsRepository;
    private final RecentReportsService recentReportsService;
    private final UserService userService;

    @Override
    public LoanDto saveLoan(LoanDto loan, Company company, Long idUser) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        loan.setCompany(company);
        loan.setCreatedBy(userService.findByID(idUser));
        return LoanMapper.toLoanDto(loanRepository.save(LoanMapper.toLoan(loan)));
    }

    @Override
    public LoanDto UpdateLoan(LoanDto loan, Company company, Long idUser) throws ValidationException {
        if (loan.getId() == null)
            throw new ValidationException(List.of(ErrorMessage.builder().message("Loan ID is required").build()));
        else
        {
            Loan loan1 = loanRepository.findById(loan.getId()).get();
            loan1.setAmount(loan.getAmount());
            loan1.setCollateral(loan.getCollateral());
            loan1.setCurrency(loan.getCurrency());
            loan1.setCustomerName(loan.getCustomerName());
            loan1.setDueDate(loan.getDueDate());
            loan1.setInterestRate(loan.getInterestRate());
            loan1.setIssueDate(loan.getIssueDate());
            loan1.setNotes(loan.getNotes());
            loan1.setStatus(loan.getStatus());
            loan1.setIsConfidential(loan.getIsConfidential());
            loan1.setCompany(company);
            loan1.setUpdatedAt(new Date());
            return LoanMapper.toLoanDto(loanRepository.save(loan1));
        }
    }

    @Override
    public Page<LoanDto> getPaginatedLoans(Pageable pageable, Company company) {
        Page<Loan> loanPage = loanRepository.findByCompanyOrderByIdDesc(company, pageable);
        return loanPage.map(LoanMapper::toLoanDto);
    }

    @Override
    public Page<LoanDto> getLoanList(String searchTerm, String status, String date, String currency, String amountMin, String amountMax,
                                     Pageable pageable, Company company) {

        Specification<Loan> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add company filter
            predicates.add(criteriaBuilder.equal(root.get("company"), company));

            // Search term filter (search in multiple fields)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();

                // Try to parse ID if numeric
                try {
                    Long idValue = Long.parseLong(searchTerm);
                    searchPredicates.add(criteriaBuilder.equal(root.get("id"), idValue));
                } catch (NumberFormatException e) {
                    // If not numeric, search by string representation
                    searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("id").as(String.class)), searchPattern));
                }

                // Search in customer name
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), searchPattern));

                // Search in currency
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("currency")), searchPattern));

                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }

            // Status filter
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (amountMin != null && !amountMin.isEmpty()) {
                BigDecimal minAmount = new BigDecimal(amountMin);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (amountMax != null && !amountMax.isEmpty()) {
                BigDecimal maxAmount = new BigDecimal(amountMax);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            // Date filter
            if (date != null && !date.isEmpty()) {
                try {
                    LocalDate localDate = LocalDate.parse(date);
                    LocalDateTime startOfDay = localDate.atStartOfDay();
                    LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

                    // Filter loans either by issue date or due date
                    Predicate issueDatePredicate = criteriaBuilder.between(
                            root.get("issueDate"), startOfDay, endOfDay);
                    Predicate dueDatePredicate = criteriaBuilder.between(
                            root.get("dueDate"), startOfDay, endOfDay);

                    predicates.add(criteriaBuilder.or(issueDatePredicate, dueDatePredicate));
                } catch (Exception e) {
                    // Log parsing error
                    System.err.println("Error parsing date: " + date);
                }
            }

            // Currency filter
            if (currency != null && !currency.isEmpty() && !currency.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("currency"), currency));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Add default sorting by id in descending order
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Loan> loanPage = loanRepository.findAll(spec, pageableWithSort);
        return loanPage.map(LoanMapper::toLoanDto);
    }

    @Override
    public boolean deleteLoans(List<Long> id, Company company) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        for (Long loanId : id) {
            Optional<Loan> loan = loanRepository.findById(loanId);
            if (loan.isEmpty() || !loan.get().getCompany().equals(company)) {
                errorMessages.add(ErrorMessage.builder().message("Loan not found").build());
            }
        }

        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }

        for (Long loanId : id) {
            loanRepository.deleteById(loanId);
        }

        return true;
    }

    @Override
    public boolean changeLoanStatus(Long id, LoanStatus status, Company company) throws ValidationException {
        if (loanRepository.findById(id).isEmpty()) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Loan not found").build()));
        }

        Loan loan = loanRepository.findById(id).get();
        loan.setStatus(status);
        loanRepository.save(loan);

        return true;
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateLoanReport(ReportsDto reportsDto, Company company) throws ValidationException {
        try {
            Specification<Loan> spec = (root, query, criteriaBuilder) -> {
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
            List<Loan> loans = loanRepository.findAll(spec, sort);

            String format = reportsDto.getFormat() != null ? reportsDto.getFormat().toLowerCase() : "excel";

            switch (format) {
                case "excel":
                    return generateExcelReport(loans, company, reportsDto);
                case "pdf":
                    return generateExcelReport(loans, company, reportsDto);
                case "csv":
                    return generateCsvReport(loans, company, reportsDto);
                default:
                    return generateExcelReport(loans, company, reportsDto);
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
     * Generate Excel report from Loan data
     */
    private ResponseEntity<ByteArrayResource> generateExcelReport(List<Loan> loans, Company company, ReportsDto reportsDto) throws IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Loans Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "ID", "Client", "Montant", "Devise", "Date d'émission", "Date d'échéance",
                "Taux d'intérêt", "Statut", "Garantie", "Notes", "Confidentiel"
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
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));

        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));

        // Add data rows
        int rowNum = 1;
        for (Loan loan : loans) {
            Row row = sheet.createRow(rowNum++);

            // ID
            row.createCell(0).setCellValue(loan.getId());

            // Customer Name
            row.createCell(1).setCellValue(loan.getCustomerName() != null ? loan.getCustomerName() : "");

            // Amount
            Cell amountCell = row.createCell(2);
            if (loan.getAmount() != null) {
                amountCell.setCellValue(loan.getAmount().doubleValue());
                amountCell.setCellStyle(currencyStyle);
            }

            // Currency
            row.createCell(3).setCellValue(loan.getCurrency() != null ? loan.getCurrency() : "");

            // Issue Date
            Cell issueDateCell = row.createCell(4);
            if (loan.getIssueDate() != null) {
                issueDateCell.setCellValue(java.sql.Date.valueOf(loan.getIssueDate()));
                issueDateCell.setCellStyle(dateCellStyle);
            }

            // Due Date
            Cell dueDateCell = row.createCell(5);
            if (loan.getDueDate() != null) {
                dueDateCell.setCellValue(java.sql.Date.valueOf(loan.getDueDate()));
                dueDateCell.setCellStyle(dateCellStyle);
            }

            // Interest Rate
            Cell interestRateCell = row.createCell(6);
            if (loan.getInterestRate() != null) {
                // Convertir le taux d'intérêt en décimal pour le format pourcentage
                interestRateCell.setCellValue(loan.getInterestRate().doubleValue() / 100.0);
                interestRateCell.setCellStyle(percentStyle);
            }

            // Status
            row.createCell(7).setCellValue(loan.getStatus() != null ? loan.getStatus().toString() : "");

            // Collateral
            row.createCell(8).setCellValue(loan.getCollateral() != null ? loan.getCollateral() : "");

            // Notes
            row.createCell(9).setCellValue(loan.getNotes() != null ? loan.getNotes() : "");

            // Is Confidential
            row.createCell(10).setCellValue(loan.getIsConfidential() != null && loan.getIsConfidential() ? "Oui" : "Non");
        }

        // Add summary rows
        rowNum += 2; // Leave a blank row

        // Summary by Currency
        Row summaryTitleRow = sheet.createRow(rowNum++);
        CellStyle summaryTitleStyle = workbook.createCellStyle();
        Font summaryTitleFont = workbook.createFont();
        summaryTitleFont.setBold(true);
        summaryTitleStyle.setFont(summaryTitleFont);

        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("Récapitulatif par devise :");
        summaryTitleCell.setCellStyle(summaryTitleStyle);

        // Group by currency
        Map<String, List<Loan>> loansByCurrency = loans.stream()
                .filter(loan -> loan.getCurrency() != null)
                .collect(Collectors.groupingBy(Loan::getCurrency));

        // Add summary headers
        Row summaryHeaderRow = sheet.createRow(rowNum++);
        summaryHeaderRow.createCell(0).setCellValue("Devise");
        summaryHeaderRow.createCell(1).setCellValue("Nombre de prêts");
        summaryHeaderRow.createCell(2).setCellValue("Montant total");

        // Add summary data
        for (Map.Entry<String, List<Loan>> entry : loansByCurrency.entrySet()) {
            Row summaryRow = sheet.createRow(rowNum++);

            summaryRow.createCell(0).setCellValue(entry.getKey());
            summaryRow.createCell(1).setCellValue(entry.getValue().size());

            // Calculate total amount
            BigDecimal totalAmount = entry.getValue().stream()
                    .filter(loan -> loan.getAmount() != null)
                    .map(Loan::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Cell totalAmountCell = summaryRow.createCell(2);
            totalAmountCell.setCellValue(totalAmount.doubleValue());
            totalAmountCell.setCellStyle(currencyStyle);
        }

        // Summary by Status
        rowNum += 2; // Leave a blank row
        Row statusTitleRow = sheet.createRow(rowNum++);
        Cell statusTitleCell = statusTitleRow.createCell(0);
        statusTitleCell.setCellValue("Récapitulatif par statut :");
        statusTitleCell.setCellStyle(summaryTitleStyle);

        // Group by status
        Map<LoanStatus, List<Loan>> loansByStatus = loans.stream()
                .filter(loan -> loan.getStatus() != null)
                .collect(Collectors.groupingBy(Loan::getStatus));

        // Add status summary headers
        Row statusHeaderRow = sheet.createRow(rowNum++);
        statusHeaderRow.createCell(0).setCellValue("Statut");
        statusHeaderRow.createCell(1).setCellValue("Nombre de prêts");

        // Add status summary data
        for (Map.Entry<LoanStatus, List<Loan>> entry : loansByStatus.entrySet()) {
            Row summaryRow = sheet.createRow(rowNum++);

            summaryRow.createCell(0).setCellValue(entry.getKey().toString());
            summaryRow.createCell(1).setCellValue(entry.getValue().size());
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
        String fileName = "loans_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Loan Report"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * Generate PDF report from Loan data
     * Note: To fix the FontFamily issue, we'll use FontFactory instead
     */
    private ResponseEntity<ByteArrayResource> generatePdfReport(List<Loan> loans) throws IOException {
        try {
            // Create a new PDF document
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Add a title
            Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des Prêts", (com.itextpdf.text.Font) titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date
            Font dateFont = (Font) FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph dateParagraph = new Paragraph("Généré le : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), (com.itextpdf.text.Font) dateFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.setSpacingAfter(20);
            document.add(dateParagraph);

            // Add company information if available
            Font companyFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            if (loans.size() > 0 && loans.get(0).getCompany() != null) {
                Company company = loans.get(0).getCompany();
                Paragraph companyParagraph = new Paragraph("Entreprise : " + company.getName(), (com.itextpdf.text.Font) companyFont);
                companyParagraph.setAlignment(Element.ALIGN_LEFT);
                companyParagraph.setSpacingAfter(10);
                document.add(companyParagraph);
            }

            // Create the table with columns
            PdfPTable table = new PdfPTable(10); // ID, Client, Montant, Devise, Date émission, Date échéance, Taux, Statut, Garantie, Notes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths (adjust as needed)
            float[] columnWidths = {0.5f, 1.5f, 1f, 0.8f, 1f, 1f, 0.8f, 1f, 1.5f, 2f};
            table.setWidths(columnWidths);

            // Add table header
            Font headerFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            String[] headers = {"ID", "Client", "Montant", "Devise", "Émission", "Échéance", "Taux", "Statut", "Garantie", "Notes"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, (com.itextpdf.text.Font) headerFont));
                cell.setBackgroundColor(new BaseColor(13, 110, 253)); // Bootstrap primary color
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            // Add data rows
            Font cellFont = (Font) FontFactory.getFont(FontFactory.HELVETICA, 9);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Loan loan : loans) {
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(loan.getId()), (com.itextpdf.text.Font) cellFont));
                idCell.setPadding(5);
                table.addCell(idCell);

                // Customer Name
                PdfPCell nameCell = new PdfPCell(new Phrase(loan.getCustomerName() != null ? loan.getCustomerName() : "", (com.itextpdf.text.Font) cellFont));
                nameCell.setPadding(5);
                table.addCell(nameCell);

                // Amount
                PdfPCell amountCell = new PdfPCell(new Phrase(
                        loan.getAmount() != null ? String.format("%,.2f", loan.getAmount()) : "", (com.itextpdf.text.Font) cellFont));
                amountCell.setPadding(5);
                amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(amountCell);

                // Currency
                PdfPCell currencyCell = new PdfPCell(new Phrase(loan.getCurrency() != null ? loan.getCurrency() : "", (com.itextpdf.text.Font) cellFont));
                currencyCell.setPadding(5);
                table.addCell(currencyCell);

                // Issue Date
                PdfPCell issueDateCell = new PdfPCell(new Phrase(
                        loan.getIssueDate() != null ? loan.getIssueDate().toString() : "", (com.itextpdf.text.Font) cellFont));
                issueDateCell.setPadding(5);
                table.addCell(issueDateCell);

                // Due Date
                PdfPCell dueDateCell = new PdfPCell(new Phrase(
                        loan.getDueDate() != null ? loan.getDueDate().toString() : "", (com.itextpdf.text.Font) cellFont));
                dueDateCell.setPadding(5);
                table.addCell(dueDateCell);

                // Interest Rate
                PdfPCell rateCell = new PdfPCell(new Phrase(
                        loan.getInterestRate() != null ? String.format("%.2f%%", loan.getInterestRate()) : "", (com.itextpdf.text.Font) cellFont));
                rateCell.setPadding(5);
                rateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(rateCell);

                // Status
                PdfPCell statusCell = new PdfPCell(new Phrase(
                        loan.getStatus() != null ? loan.getStatus().toString() : "", (com.itextpdf.text.Font) cellFont));
                statusCell.setPadding(5);
                table.addCell(statusCell);

                // Collateral
                PdfPCell collateralCell = new PdfPCell(new Phrase(
                        loan.getCollateral() != null ? loan.getCollateral() : "", (com.itextpdf.text.Font) cellFont));
                collateralCell.setPadding(5);
                table.addCell(collateralCell);

                // Notes
                PdfPCell notesCell = new PdfPCell(new Phrase(
                        loan.getNotes() != null ? loan.getNotes() : "", (com.itextpdf.text.Font) cellFont));
                notesCell.setPadding(5);
                table.addCell(notesCell);
            }

            // Add the table to the document
            document.add(table);

            // Add summary by currency
            Font summaryFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph summaryTitle = new Paragraph("Récapitulatif par devise", (com.itextpdf.text.Font) summaryFont);
            summaryTitle.setSpacingBefore(20);
            summaryTitle.setSpacingAfter(10);
            document.add(summaryTitle);

            // Group by currency
            Map<String, List<Loan>> loansByCurrency = loans.stream()
                    .filter(loan -> loan.getCurrency() != null)
                    .collect(Collectors.groupingBy(Loan::getCurrency));

            // Create summary table for currencies
            PdfPTable currencyTable = new PdfPTable(3);
            currencyTable.setWidthPercentage(70);
            currencyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            currencyTable.setSpacingBefore(10f);
            currencyTable.setSpacingAfter(10f);

            // Currency table header
            Font summaryHeaderFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            PdfPCell currencyHeaderCell = new PdfPCell(new Phrase("Devise", (com.itextpdf.text.Font) summaryHeaderFont));
            currencyHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            currencyHeaderCell.setPadding(5);
            currencyTable.addCell(currencyHeaderCell);

            PdfPCell countHeaderCell = new PdfPCell(new Phrase("Nombre de prêts", (com.itextpdf.text.Font) summaryHeaderFont));
            countHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            countHeaderCell.setPadding(5);
            currencyTable.addCell(countHeaderCell);

            PdfPCell totalHeaderCell = new PdfPCell(new Phrase("Montant total", (com.itextpdf.text.Font) summaryHeaderFont));
            totalHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalHeaderCell.setPadding(5);
            currencyTable.addCell(totalHeaderCell);

            // Add currency summary data
            Font summaryDataFont = (Font) FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Map.Entry<String, List<Loan>> entry : loansByCurrency.entrySet()) {
                PdfPCell currencyCell = new PdfPCell(new Phrase(entry.getKey(), (com.itextpdf.text.Font) summaryDataFont));
                currencyCell.setPadding(5);
                currencyTable.addCell(currencyCell);

                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue().size()), (com.itextpdf.text.Font) summaryDataFont));
                countCell.setPadding(5);
                currencyTable.addCell(countCell);

                // Calculate total amount
                BigDecimal totalAmount = entry.getValue().stream()
                        .filter(loan -> loan.getAmount() != null)
                        .map(Loan::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%,.2f", totalAmount), (com.itextpdf.text.Font) summaryDataFont));
                totalCell.setPadding(5);
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                currencyTable.addCell(totalCell);
            }

            document.add(currencyTable);

            // Add summary by status
            Paragraph statusTitle = new Paragraph("Récapitulatif par statut", (com.itextpdf.text.Font) summaryFont);
            statusTitle.setSpacingBefore(20);
            statusTitle.setSpacingAfter(10);
            document.add(statusTitle);

            // Group by status
            Map<LoanStatus, List<Loan>> loansByStatus = loans.stream()
                    .filter(loan -> loan.getStatus() != null)
                    .collect(Collectors.groupingBy(Loan::getStatus));

            // Create summary table for status
            PdfPTable statusTable = new PdfPTable(2);
            statusTable.setWidthPercentage(50);
            statusTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            statusTable.setSpacingBefore(10f);
            statusTable.setSpacingAfter(10f);

            // Status table header
            PdfPCell statusHeaderCell = new PdfPCell(new Phrase("Statut", (com.itextpdf.text.Font) summaryHeaderFont));
            statusHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            statusHeaderCell.setPadding(5);
            statusTable.addCell(statusHeaderCell);

            PdfPCell statusCountHeaderCell = new PdfPCell(new Phrase("Nombre de prêts", (com.itextpdf.text.Font) summaryHeaderFont));
            statusCountHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            statusCountHeaderCell.setPadding(5);
            statusTable.addCell(statusCountHeaderCell);

            // Add status summary data
            for (Map.Entry<LoanStatus, List<Loan>> entry : loansByStatus.entrySet()) {
                PdfPCell statusCell = new PdfPCell(new Phrase(entry.getKey().toString(), (com.itextpdf.text.Font) summaryDataFont));
                statusCell.setPadding(5);
                statusTable.addCell(statusCell);

                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue().size()), (com.itextpdf.text.Font) summaryDataFont));
                countCell.setPadding(5);
                statusTable.addCell(countCell);
            }

            document.add(statusTable);

            // Close the document
            document.close();

            // Create resource from byte array
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // Create file name with timestamp
            String fileName = "loans_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

            // Return file as downloadable response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (DocumentException e) {
            log.error("Error generating PDF report", e);
            throw new IOException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    /**
     * Generate CSV report from Loan data
     */
    private ResponseEntity<ByteArrayResource> generateCsvReport(List<Loan> loans, Company company, ReportsDto reportsDto) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        // Add header
        csvContent.append("ID,Client,Montant,Devise,Date d'émission,Date d'échéance,Taux d'intérêt,Statut,Garantie,Notes,Confidentiel\n");

        // Add data rows
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Loan loan : loans) {
            // ID
            csvContent.append(loan.getId()).append(",");

            // Customer Name
            csvContent.append(escapeSpecialCharacters(loan.getCustomerName() != null ? loan.getCustomerName() : "")).append(",");

            // Amount
            if (loan.getAmount() != null) {
                csvContent.append(loan.getAmount());
            }
            csvContent.append(",");

            // Currency
            csvContent.append(loan.getCurrency() != null ? loan.getCurrency() : "").append(",");

            // Issue Date
            if (loan.getIssueDate() != null) {
                csvContent.append(loan.getIssueDate());
            }
            csvContent.append(",");

            // Due Date
            if (loan.getDueDate() != null) {
                csvContent.append(loan.getDueDate());
            }
            csvContent.append(",");

            // Interest Rate
            if (loan.getInterestRate() != null) {
                csvContent.append(loan.getInterestRate()).append("%");
            }
            csvContent.append(",");

            // Status
            csvContent.append(loan.getStatus() != null ? loan.getStatus() : "").append(",");

            // Collateral
            csvContent.append(escapeSpecialCharacters(loan.getCollateral() != null ? loan.getCollateral() : "")).append(",");

            // Notes
            csvContent.append(escapeSpecialCharacters(loan.getNotes() != null ? loan.getNotes() : "")).append(",");

            // Is Confidential
            csvContent.append(loan.getIsConfidential() != null && loan.getIsConfidential() ? "Oui" : "Non");

            csvContent.append("\n");
        }

        // Add summary section for currency
        csvContent.append("\n\nRécapitulatif par devise\n");
        csvContent.append("Devise,Nombre de prêts,Montant total\n");

        // Group by currency
        Map<String, List<Loan>> loansByCurrency = loans.stream()
                .filter(loan -> loan.getCurrency() != null)
                .collect(Collectors.groupingBy(Loan::getCurrency));

        // Add currency summary data
        for (Map.Entry<String, List<Loan>> entry : loansByCurrency.entrySet()) {
            csvContent.append(entry.getKey()).append(",");
            csvContent.append(entry.getValue().size()).append(",");

            // Calculate total amount
            BigDecimal totalAmount = entry.getValue().stream()
                    .filter(loan -> loan.getAmount() != null)
                    .map(Loan::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            csvContent.append(totalAmount).append("\n");
        }

        // Add summary section for status
        csvContent.append("\n\nRécapitulatif par statut\n");
        csvContent.append("Statut,Nombre de prêts\n");

        // Group by status
        Map<LoanStatus, List<Loan>> loansByStatus = loans.stream()
                .filter(loan -> loan.getStatus() != null)
                .collect(Collectors.groupingBy(Loan::getStatus));

        // Add status summary data
        for (Map.Entry<LoanStatus, List<Loan>> entry : loansByStatus.entrySet()) {
            csvContent.append(entry.getKey()).append(",");
            csvContent.append(entry.getValue().size()).append("\n");
        }

        // Convert to byte array
        byte[] bytes = csvContent.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        // Create file name with timestamp
        String fileName = "loans_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Loan Report"));

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
