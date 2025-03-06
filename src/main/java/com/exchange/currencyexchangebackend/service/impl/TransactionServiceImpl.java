package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.TransactionDto;
import com.exchange.currencyexchangebackend.model.dto.TransactionStatistic;
import com.exchange.currencyexchangebackend.model.entity.FundBalance;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.LoanStatus;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import com.exchange.currencyexchangebackend.model.enums.TransactionStatus;
import com.exchange.currencyexchangebackend.model.mapper.FundBalanceMapper;
import com.exchange.currencyexchangebackend.model.mapper.TransactionMapper;
import com.exchange.currencyexchangebackend.repository.LoanRepository;
import jakarta.persistence.criteria.Predicate;
import com.exchange.currencyexchangebackend.repository.TransactionRepository;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final FundBalanceService fundBalanceService;

    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        transactionDto.setStatus(TransactionStatus.COMPLETED);
        transactionDto.setCreatedAt(new Date());
        transactionDto.setCreatedAt(new Date());
        transactionDto.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        transactionDto.setMonth(new SimpleDateFormat("MMMM").format(new Date()));
        transactionDto.setYear(new SimpleDateFormat("yyyy").format(new Date()));
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
        fundBalanceService.saveFundBalance(FundBalanceMapper.toDto(fundBalanceWithdraw));
        fundBalanceService.saveFundBalance(FundBalanceMapper.toDto(fundBalanceDeposit));
        return transactionDto;
    }

    @Override
    public List<TransactionDto> getTransactionList() {
        return TransactionMapper.toTransactionDtos(transactionRepository.findAll());
    }

    @Override
    public Page<TransactionDto> getPaginatedTransactions(Pageable pageable) {
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
        return transactionPage.map(TransactionMapper::toTransactionDto);
    }

    @Override
    public Page<TransactionDto> getFilteredTransactions(String searchTerm, String status, String dateStr, String currency, Pageable pageable) {
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

                // Remove or comment out this section since customerID doesn't exist
                // if (root.get("customerID") != null) {
                //     searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customerID")), searchPattern));
                // }

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

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
        return transactionPage.map(TransactionMapper::toTransactionDto);
    }

    @Override
    public TransactionStatistic getTransactionStatistics() {
        TransactionStatistic transactionStatistic = new TransactionStatistic();
        transactionStatistic.setTotalExchanges(transactionRepository.countByMonth(new SimpleDateFormat("MMMM").format(new Date())));
        transactionStatistic.setActiveLoans(loanRepository.countByStatus(LoanStatus.ACTIVE));
        transactionStatistic.setTodayProfit(BigDecimal.valueOf(01));
        transactionStatistic.setAvailableFunds(BigDecimal.valueOf(01));
        return transactionStatistic;
    }
}
