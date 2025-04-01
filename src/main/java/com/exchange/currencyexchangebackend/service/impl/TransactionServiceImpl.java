package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionDtoFilter;
import com.exchange.currencyexchangebackend.model.dto.TransactionStatistic;
import com.exchange.currencyexchangebackend.model.entity.*;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import com.exchange.currencyexchangebackend.model.mapper.FundBalanceMapper;
import com.exchange.currencyexchangebackend.model.mapper.HistoryMapper;
import com.exchange.currencyexchangebackend.model.mapper.TransactionMapper;
import com.exchange.currencyexchangebackend.repository.HistoryRepository;
import com.exchange.currencyexchangebackend.repository.LoanRepository;
import com.exchange.currencyexchangebackend.repository.RecentReportsRepository;
import com.exchange.currencyexchangebackend.service.RecentReportsService;
import com.google.common.net.HttpHeaders;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import com.exchange.currencyexchangebackend.repository.TransactionRepository;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final HistoryRepository historyRepository;
    private final LoanRepository loanRepository;
    private final FundBalanceService fundBalanceService;
    private final RecentReportsRepository recentReportsRepository;
    private final RecentReportsService recentReportsService;

    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto, Company company, User user) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        transactionDto.setStatus(TransactionStatus.COMPLETED);
        transactionDto.setCreatedAt(new Date());
        transactionDto.setUpdatedAt(new Date());
        transactionDto.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        transactionDto.setMonth(new SimpleDateFormat("MMMM").format(new Date()));
        transactionDto.setYear(new SimpleDateFormat("yyyy").format(new Date()));
        transactionDto.setCompany(company);
        transactionDto.setCreatedBy(user);
        if (transactionDto.getCustomerName() == null || transactionDto.getCustomerName().isEmpty())
            transactionDto.setCustomerName("Utilisateur" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        if (transactionRepository.save(TransactionMapper.toTransaction(transactionDto)) == null) {
            errorMessages.add(ErrorMessage.builder().status(400).message("Transaction not saved").build());
            errorMessages.add(ErrorMessage.builder().message("Transaction not saved").build());
        }
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        // for withdraw
        FundBalance fundBalanceWithdraw = new FundBalance();
        fundBalanceWithdraw.setAmount(transactionDto.getFromAmount());
        fundBalanceWithdraw.setCurrency(Currency.valueOf(transactionDto.getFromCurrency()));
        fundBalanceWithdraw.setOperationFunds(OperationFunds.withdraw);
        fundBalanceWithdraw.setNotes("Withdraw from " + transactionDto.getFromCurrency() + " to " + transactionDto.getToCurrency());

        // for deposit
        FundBalance fundBalanceDeposit = new FundBalance();
        fundBalanceDeposit.setAmount(transactionDto.getToAmount());
        fundBalanceDeposit.setCurrency(Currency.valueOf(transactionDto.getToCurrency()));
        fundBalanceDeposit.setOperationFunds(OperationFunds.add);
        fundBalanceDeposit.setNotes("Deposit from " + transactionDto.getFromCurrency() + " to " + transactionDto.getToCurrency());
        fundBalanceService.saveFundBalance(FundBalanceMapper.toDto(fundBalanceWithdraw), company, user.getId());
        fundBalanceService.saveFundBalance(FundBalanceMapper.toDto(fundBalanceDeposit), company, user.getId());
        RecentActivities recentActivities = new RecentActivities();
        recentActivities.setAction("Transaction");
        recentActivities.setDescription("Transaction from " + transactionDto.getFromCurrency() + " to " + transactionDto.getToCurrency());
        recentActivities.setIcon("fa-user-plus");
        recentActivities.setType("success");
        recentActivities.setKind("transaction");
        recentActivities.setCompany(company);
        recentActivities.setTime(new Date());
        recentReportsService.saveRecentActivities(recentActivities, company);
        return transactionDto;
    }

    @Override
    public List<TransactionDto> getTransactionList() {
        return TransactionMapper.toTransactionDtos(transactionRepository.findAll());
    }

    @Override
    public Page<TransactionDto> getPaginatedTransactions(Pageable pageable, Company company) {
        Page<Transaction> transactionPage = transactionRepository.PaginatedTransactionsWithCompany(pageable, company);
        return transactionPage.map(TransactionMapper::toTransactionDto);
    }

    @Override
    public Page<TransactionDto> getFilteredTransactions(String searchTerm, String status, String dateStr, String currency, Pageable pageable, Company company) {
        Specification<Transaction> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search term filter (search in multiple fields)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();

                // For id field - first check if the search term can be converted to a number
                try {
                    Long idValue = Long.parseLong(searchTerm);
                    // If it can be parsed as a number, search using equals rather than like
                    searchPredicates.add(criteriaBuilder.equal(root.get("id"), idValue));
                } catch (NumberFormatException e) {
                    // If it's not a number, search using string representation
                    searchPredicates.add(criteriaBuilder.like(root.get("id").as(String.class), searchPattern));
                }

                // Other string fields - make sure these exist in your entity
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), searchPattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fromCurrency")), searchPattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("toCurrency")), searchPattern));

                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }

            // Status filter
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Date filter
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.atTime(23, 59, 59);

                    predicates.add(criteriaBuilder.equal(root.get("date"), dateStr));
                } catch (Exception e) {
                    // Log parsing error
                    System.err.println("Error parsing date: " + dateStr);
                }
            }

            // Currency filter
            if (currency != null && !currency.isEmpty() && !currency.equals("all")) {
                Predicate fromCurrencyPredicate = criteriaBuilder.equal(root.get("fromCurrency"), currency);
                Predicate toCurrencyPredicate = criteriaBuilder.equal(root.get("toCurrency"), currency);
                predicates.add(criteriaBuilder.or(fromCurrencyPredicate, toCurrencyPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // إنشاء pageable جديدة مع ترتيب من الأحدث إلى الأقدم
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Transaction> transactionPage = transactionRepository.findAll(
                Specification.where(spec)
                        .and((root, query, cb) -> cb.equal(root.get("company"), company)),
                pageableWithSort
        );
        return transactionPage.map(TransactionMapper::toTransactionDto);
    }

    @Override
    public TransactionStatistic getTransactionStatistics(Company company) {
        TransactionStatistic transactionStatistic = new TransactionStatistic();
        String currentMonth = new SimpleDateFormat("MMMM").format(new Date());

        // Get the previous month
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String previousMonth = new SimpleDateFormat("MMMM").format(cal.getTime());

        // Fetch the total exchanges for the current and previous month
        BigDecimal currentMonthExchanges = transactionRepository.countByMonthAndCompany(currentMonth, company);
        BigDecimal previousMonthExchanges = transactionRepository.countByMonthAndCompany(previousMonth, company);

        // Calculate the percentage change
        BigDecimal exchangesTrend = BigDecimal.ZERO;
        if (previousMonthExchanges.compareTo(BigDecimal.ZERO) != 0) {
            exchangesTrend = currentMonthExchanges.subtract(previousMonthExchanges)
                    .divide(previousMonthExchanges, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        transactionStatistic.setTotalExchanges(currentMonthExchanges);
        transactionStatistic.setActiveLoans(loanRepository.countByStatus(LoanStatus.ACTIVE, company));
        transactionStatistic.setTodayProfit(BigDecimal.valueOf(01));
        transactionStatistic.setAvailableFunds(BigDecimal.valueOf(01));
        transactionStatistic.setExchangesTrend(exchangesTrend);
        transactionStatistic.setCompletedTransactions(transactionRepository.countByStatusAndCompany(TransactionStatus.COMPLETED, company));
        transactionStatistic.setPendingTransactions(transactionRepository.countByStatusAndCompany(TransactionStatus.PENDING, company));
        transactionStatistic.setCanceledTransactions(transactionRepository.countByStatusAndCompany(TransactionStatus.CANCELED, company));

        return transactionStatistic;
    }

    @Override
    public List<TransactionDto> getRecentTransactions(Company company) {
        return TransactionMapper.toTransactionDtos(transactionRepository.findTop4ByCompanyOrderByCreatedAtDesc(company));
    }

    @Override
    public int getRecentCountTransactionsWithDay(String day, Company company) {
        return transactionRepository.getRecentCountTransactionsWithDay(day, company);
    }

    @Override
    public List<TransactionDtoFilter> getRecentTransactionsLast3Months(Company company) {
        List<TransactionDtoFilter> transactionDtoFilters = new ArrayList<>();
        List<Object[]> transactions = transactionRepository.getRecentTransactionsLast3Months(company.getId());

        for (Object[] transaction : transactions) {

            TransactionDtoFilter dto = new TransactionDtoFilter();
            dto.setMonths(transaction[2] != null ? transaction[2].toString() : null);
            dto.setCount(transaction[3] != null ? Long.parseLong(transaction[3].toString()) : 0L);
            dto.setWeekLabel(transaction[4] != null ? transaction[4].toString() : null);

            transactionDtoFilters.add(dto);
        }

        return transactionDtoFilters;
    }

    @Override
    public List<TransactionDtoFilter> getRecentTransactionsLast7Days(Company company) {
        List<TransactionDtoFilter> transactionDtoFilters = new ArrayList<>();
        List<Object[]> transactions = transactionRepository.getRecentTransactionsLast7Days(company.getId());

        for (Object[] transaction : transactions) {
            TransactionDtoFilter dto = new TransactionDtoFilter();
            // Get days at index 0
            dto.setDays(transaction[0] != null ? transaction[0].toString() : null);
            // Skip weeks (index 1) since it's NULL
            // Skip months (index 2) since it's NULL
            // Get count at index 3
            dto.setCount(transaction[3] != null ? Long.parseLong(transaction[3].toString()) : 0L);
            // Skip WeekLabel (index 4) since it's NULL or set it to the day name if you prefer
            dto.setWeekLabel(transaction[0] != null ? transaction[0].toString() : null);

            transactionDtoFilters.add(dto);
        }

        return transactionDtoFilters;
    }

    @Override
    public List<TransactionDtoFilter> getRecentTransactionsLast4Weeks(Company company) {
        List<TransactionDtoFilter> transactionDtoFilters = new ArrayList<>();
        List<Object[]> transactions = transactionRepository.getRecentTransactionsLast4Weeks(company.getId());

        for (Object[] transaction : transactions) {
            TransactionDtoFilter dto = new TransactionDtoFilter();
            // Skip days (index 0) since it's NULL
            // Get weeks at index 1
            dto.setWeeks(transaction[1] != null ? transaction[1].toString() : null);
            // Skip months (index 2) since it's NULL
            // Get count at index 3
            dto.setCount(transaction[3] != null ? Long.parseLong(transaction[3].toString()) : 0L);
            // Get WeekLabel at index 4
            dto.setWeekLabel(transaction[4] != null ? transaction[4].toString() : null);

            transactionDtoFilters.add(dto);
        }

        return transactionDtoFilters;
    }

    @Override
    public List<TransactionDtoFilter> getRecentTransactionsLast12Months(Company company) {
        List<TransactionDtoFilter> transactionDtoFilters = new ArrayList<>();
        List<Object[]> transactions = transactionRepository.getRecentTransactionsLast12Months(company.getId());

        for (Object[] transaction : transactions) {
            TransactionDtoFilter dto = new TransactionDtoFilter();
            // Skip days (index 0) since it's NULL
            // Skip weeks (index 1) since it's NULL
            // Get months at index 2
            dto.setMonths(transaction[2] != null ? transaction[2].toString() : null);
            // Get count at index 3
            dto.setCount(transaction[3] != null ? Long.parseLong(transaction[3].toString()) : 0L);
            // Get WeekLabel at index 4 (which contains the month abbreviation)
            dto.setWeekLabel(transaction[4] != null ? transaction[4].toString() : null);

            transactionDtoFilters.add(dto);
        }

        return transactionDtoFilters;
    }

    @Override
    public boolean deleteTransactions(List<Long> id,  User user, Company company) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        for (Long transactionId : id) {
            Optional<Transaction> transaction = transactionRepository.findById(transactionId);
            if (transaction.isPresent()) {
                transactionRepository.delete(transaction.get());
                History history = new History();
                history = HistoryMapper.toHistory(transaction.get());
                history.setDeletedBy(user);
                historyRepository.save(history);
            } else {
                errorMessages.add(ErrorMessage.builder().status(400).message("Transaction not found").build());
            }
        }
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        return true;
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportExcel(List<Long> ids) throws ValidationException {
        try {
            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Customer Name", "From Currency", "From Amount",
                    "To Currency", "To Amount", "Total Paid", "Created At"};

            // Create header styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Add headers
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create styles for data
            CellStyle currencyStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-mm-yyyy hh:mm:ss"));

            // Get transactions
            List<Transaction> transactions = new ArrayList<>();
            List<ErrorMessage> errorMessages = new ArrayList<>();

            for (Long transactionId : ids) {
                Optional<Transaction> transaction = transactionRepository.findById(transactionId);
                if (transaction.isPresent()) {
                    transactions.add(transaction.get());
                } else {
                    errorMessages.add(ErrorMessage.builder()
                            .status(400)
                            .message("Transaction with ID " + transactionId + " not found")
                            .build());
                }
            }

            if (!errorMessages.isEmpty()) {
                throw new ValidationException(errorMessages);
            }

            // Add data rows
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getCustomerName());
                row.createCell(2).setCellValue(transaction.getFromCurrency());

                Cell fromAmountCell = row.createCell(3);
                fromAmountCell.setCellValue(transaction.getFromAmount().doubleValue());
                fromAmountCell.setCellStyle(currencyStyle);

                row.createCell(4).setCellValue(transaction.getToCurrency());

                Cell toAmountCell = row.createCell(5);
                toAmountCell.setCellValue(transaction.getToAmount().doubleValue());
                toAmountCell.setCellStyle(currencyStyle);

                Cell totalPaidCell = row.createCell(6);
                totalPaidCell.setCellValue(transaction.getTotalPaid().doubleValue());
                totalPaidCell.setCellStyle(currencyStyle);

                Cell dateCell = row.createCell(7);
                dateCell.setCellValue(transaction.getCreatedAt());
                dateCell.setCellStyle(dateStyle);
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream instead of file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // Create resource from byte array
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            // Create file name with timestamp
            String fileName = "transactions_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";

            // Return file as downloadable response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IOException e) {
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(ErrorMessage.builder()
                    .status(500)
                    .message("Error creating Excel file: " + e.getMessage())
                    .build());
            throw new ValidationException(errorMessages);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateTransactionReport(ReportsDto reportsDto, Company company) throws ValidationException {
        try {
            Specification<Transaction> spec = (root, query, criteriaBuilder) -> {
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
                            root.get("fromCurrency"),
                            reportsDto.getCurrency().name());
                    Predicate toCurrencyPredicate = criteriaBuilder.equal(
                            root.get("toCurrency"),
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
            List<Transaction> transactions = transactionRepository.findAll(spec, sort);

            String format = reportsDto.getFormat() != null ? reportsDto.getFormat().toLowerCase() : "excel";

            switch (format) {
                case "excel":
                    return generateExcelReport(transactions, company, reportsDto);
                case "pdf":
                    return generatePdfReport(transactions, company, reportsDto);
                case "csv":
                    return generateCsvReport(transactions, company, reportsDto);
                default:
                    return generateExcelReport(transactions, company, reportsDto);
            }
        } catch (Exception e) {
            log.error("Error generating transaction report", e);
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(ErrorMessage.builder()
                    .status(500)
                    .message("Error generating report: " + e.getMessage())
                    .build());
            throw new ValidationException(errorMessages);
        }
    }

    @Override
    public int importTransactionsFromExcel(MultipartFile file, Authentication authentication, Company company, User user) throws IOException {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Transaction> transactions = new ArrayList<>();
        int rowCount = 0;

        // Skip header row
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Validate header row
                validateHeaderRow(row);
                continue;
            }

            // Skip empty rows by checking if all cells are empty
            boolean isEmpty = true;
            for (int i = 0; i < 8; i++) { // Check the 8 columns we expect data in
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    String cellValue = cell.toString().trim();
                    if (!cellValue.isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }
            }

            if (isEmpty) {
                continue; // Skip this row if it's empty
            }

            try {
                Transaction transaction = mapRowToTransaction(row);

                // Set required fields
                transaction.setCompany(company);
                transaction.setCreatedBy(user);
                transaction.setCreatedAt(new Date());
                transaction.setStatus(TransactionStatus.COMPLETED);

                // Set date/time fields
                transaction.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                transaction.setMonth(new SimpleDateFormat("MMMM").format(new Date()));
                transaction.setYear(new SimpleDateFormat("yyyy").format(new Date()));
                transaction.setUpdatedAt(new Date());

                transactions.add(transaction);
                rowCount++;
            } catch (Exception e) {
                throw new IOException("Error processing row " + (row.getRowNum() + 1) + ": " + e.getMessage());
            }
        }

        workbook.close();

        if (!transactions.isEmpty()) {
            transactionRepository.saveAll(transactions);
        }

        return rowCount;
    }

    @Override
    public ResponseEntity<ByteArrayResource> exportAllExcel(Company company) throws ValidationException{
        try {
            List<Transaction> transactions = transactionRepository.findAllByCompany(company);
            return generateExcelReport(transactions, company, new ReportsDto());
        } catch (Exception e) {
            log.error("Error exporting transactions to Excel", e);
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(ErrorMessage.builder()
                    .status(500)
                    .message("Error exporting transactions to Excel: " + e.getMessage())
                    .build());
            throw new ValidationException(errorMessages);
        }
    }

    private void validateHeaderRow(Row headerRow) throws IOException {
        // Expected header names in order
        String[] expectedHeaders = {
                "customerName", "exchangeRate", "fromAmount",
                "fromCurrency", "phoneNumber", "toAmount",
                "toCurrency", "totalPaid"
        };

        for (int i = 0; i < expectedHeaders.length; i++) {
            Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String header = cell.getStringCellValue().trim();

            if (!header.equalsIgnoreCase(expectedHeaders[i])) {
                throw new IOException("Invalid header at column " + (i + 1) + ". Expected '" +
                        expectedHeaders[i] + "' but found '" + header + "'");
            }
        }
    }

    private Transaction mapRowToTransaction(Row row) throws Exception {
        Transaction transaction = new Transaction();

        transaction.setCustomerName(getCellStringValue(row, 0));
        transaction.setExchangeRate(getCellBigDecimalValue(row, 1));
        transaction.setFromAmount(getCellBigDecimalValue(row, 2));
        transaction.setFromCurrency(getCellStringValue(row, 3));
        transaction.setPhoneNumber(getCellStringValue(row, 4));
        transaction.setToAmount(getCellBigDecimalValue(row, 5));
        transaction.setToCurrency(getCellStringValue(row, 6));
        transaction.setTotalPaid(getCellBigDecimalValue(row, 7));

        return transaction;
    }

    private String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private BigDecimal getCellBigDecimalValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
            default:
                return BigDecimal.ZERO;
        }
    }

    private LocalDateTime getCellDateValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            // You might need a more sophisticated date parser here
            // This is just a simple example assuming ISO format
            return LocalDateTime.parse(dateStr);
        }

        return LocalDateTime.now();
    }

    /**
     * Generate Excel report from transaction data
     */
    private ResponseEntity<ByteArrayResource> generateExcelReport(List<Transaction> transactions, Company company, ReportsDto reportsDto) throws IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "ID", "Date", "Customer Name", "From Currency", "From Amount",
                "To Currency", "To Amount", "Exchange Rate", "Status", "Notes"
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
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowNum++);

            // ID
            row.createCell(0).setCellValue(transaction.getId());

            // Date
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(transaction.getCreatedAt());
            dateCell.setCellStyle(dateCellStyle);

            // Customer Name
            row.createCell(2).setCellValue(transaction.getCustomerName());

            // From Currency
            row.createCell(3).setCellValue(transaction.getFromCurrency());

            // From Amount
            Cell fromAmountCell = row.createCell(4);
            fromAmountCell.setCellValue(transaction.getFromAmount().doubleValue());
            fromAmountCell.setCellStyle(currencyStyle);

            // To Currency
            row.createCell(5).setCellValue(transaction.getToCurrency());

            // To Amount
            Cell toAmountCell = row.createCell(6);
            toAmountCell.setCellValue(transaction.getToAmount().doubleValue());
            toAmountCell.setCellStyle(currencyStyle);

            // Exchange Rate
            Cell exchangeRateCell = row.createCell(7);
            if (transaction.getExchangeRate() != null) {
                exchangeRateCell.setCellValue(transaction.getExchangeRate().doubleValue());
                CellStyle rateStyle = workbook.createCellStyle();
                rateStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.0000"));
                exchangeRateCell.setCellStyle(rateStyle);
            }

            // Status
            row.createCell(8).setCellValue(transaction.getStatus().toString());

            // Notes
            row.createCell(9).setCellValue(transaction.getNotes() != null ? transaction.getNotes() : "");
        }

        // Add summary row
        Row summaryRow = sheet.createRow(rowNum + 1);
        Cell summaryLabelCell = summaryRow.createCell(0);
        summaryLabelCell.setCellValue("Total Transactions:");

        Cell summaryValueCell = summaryRow.createCell(1);
        summaryValueCell.setCellValue(transactions.size());

        // Bold font for summary
        CellStyle summaryStyle = workbook.createCellStyle();
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryLabelCell.setCellStyle(summaryStyle);
        summaryValueCell.setCellStyle(summaryStyle);

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
        String fileName = "transactions_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Transactions Report"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * Generate CSV report from transaction data
     */
    private ResponseEntity<ByteArrayResource> generateCsvReport(List<Transaction> transactions, Company company, ReportsDto reportsDto) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        // Add header
        csvContent.append("ID,Date,Customer Name,From Currency,From Amount,To Currency,To Amount,Exchange Rate,Status,Notes\n");

        // Add data rows
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Transaction transaction : transactions) {
            csvContent.append(transaction.getId()).append(",");
            csvContent.append(dateFormat.format(transaction.getCreatedAt())).append(",");
            csvContent.append(escapeSpecialCharacters(transaction.getCustomerName())).append(",");
            csvContent.append(transaction.getFromCurrency()).append(",");
            csvContent.append(transaction.getFromAmount()).append(",");
            csvContent.append(transaction.getToCurrency()).append(",");
            csvContent.append(transaction.getToAmount()).append(",");

            if (transaction.getExchangeRate() != null) {
                csvContent.append(transaction.getExchangeRate());
            }
            csvContent.append(",");

            csvContent.append(transaction.getStatus()).append(",");

            if (transaction.getNotes() != null) {
                csvContent.append(escapeSpecialCharacters(transaction.getNotes()));
            }

            csvContent.append("\n");
        }

        // Convert to byte array
        byte[] bytes = csvContent.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        // Create file name with timestamp
        String fileName = "transactions_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

        // Return file as downloadable response
        recentReportsRepository.save(recentReportsService.TorecentReports(reportsDto, company, "Transactions Report"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * Generate PDF report from transaction data
     * Note: This is a placeholder. In a real application, you would use a PDF generation library like iText, PDFBox, etc.
     */
    private ResponseEntity<ByteArrayResource> generatePdfReport(List<Transaction> transactions, Company company, ReportsDto reportsDto) {
        // For this example, we'll return a simple "Not implemented" message
        // In a real application, you would use a PDF generation library

        String message = "PDF report generation is not implemented yet. Please use Excel or CSV format.";
        ByteArrayResource resource = new ByteArrayResource(message.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=not_implemented.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);

        // TODO: Implement PDF generation using a library like iText, PDFBox, etc.
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
