package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.dto.TransactionDtoFilter;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.Transaction;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query("select count(t) from Transaction t where t.month = :month")
    BigDecimal countByMonthAndCompany(@Param("month") String month, Company company);
    List<Transaction> findTop4ByCompanyOrderByCreatedAtDesc(Company company);
    List<Transaction> findTop4ByOrderByCreatedAtDesc();
    @Query("select SUM(t.totalPaid) from Transaction t where t.day = :day and t.toCurrency = :currency")
    BigDecimal calculateProfitForDay(@Param("day") String day, @Param("currency") String currency);
    @Query("select COUNT(t) from Transaction t where t.day = :day and t.company = :company")
    int getRecentCountTransactionsWithDay(@Param("day") String day, Company company);
    //getRecentTransactionsLast3Months


    @Query(value = "WITH RECURSIVE date_ranges AS (\n" +
            "    SELECT DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), '%Y-%m-%d') AS day_id,\n" +
            "           DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), '%W') AS day_name,\n" +
            "           0 AS day_num\n" +
            "    UNION ALL\n" +
            "    SELECT DATE_FORMAT(DATE_ADD(STR_TO_DATE(day_id, '%Y-%m-%d'), INTERVAL 1 DAY), '%Y-%m-%d'),\n" +
            "           DATE_FORMAT(DATE_ADD(STR_TO_DATE(day_id, '%Y-%m-%d'), INTERVAL 1 DAY), '%W'),\n" +
            "           day_num + 1\n" +
            "    FROM date_ranges\n" +
            "    WHERE day_num < 6\n" +
            ")\n" +
            "SELECT d.day_name AS days,\n" +
            "       NULL AS weeks,\n" +
            "       NULL AS months,\n" +
            "       COALESCE(COUNT(t.id), 0) AS count,\n" +
            "       NULL AS WeekLabel\n" +
            "FROM date_ranges d\n" +
            "LEFT JOIN transaction t ON DATE(t.created_at) = STR_TO_DATE(d.day_id, '%Y-%m-%d') AND t.company_id = :companyId\n" +
            "GROUP BY d.day_id, d.day_name, d.day_num\n" +
            "ORDER BY d.day_num", nativeQuery = true)
    List<Object[]> getRecentTransactionsLast7Days(@Param("companyId") Long companyId);

    @Query(value = "WITH RECURSIVE date_ranges AS (\n" +
            "    SELECT DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 WEEK), '%Y-%u') AS week_id,\n" +
            "           DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 WEEK), '%Y/%m/%d') AS start_date,\n" +
            "           DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 WEEK) + INTERVAL 6 DAY, '%Y/%m/%d') AS end_date,\n" +
            "           1 AS week_num\n" +
            "    UNION ALL\n" +
            "    SELECT DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y/%m/%d'), INTERVAL 1 WEEK), '%Y-%u'),\n" +
            "           DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y/%m/%d'), INTERVAL 1 WEEK), '%Y/%m/%d'),\n" +
            "           DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y/%m/%d'), INTERVAL 1 WEEK) + INTERVAL 6 DAY, '%Y/%m/%d'),\n" +
            "           week_num + 1\n" +
            "    FROM date_ranges\n" +
            "    WHERE week_num < 4\n" +
            ")\n" +
            "SELECT NULL AS days,\n" +
            "       d.week_id AS weeks,\n" +
            "       NULL AS months,\n" +
            "       COALESCE(COUNT(t.id), 0) AS count,\n" +
            "       CONCAT('Week ', d.week_num) AS WeekLabel\n" +
            "FROM date_ranges d\n" +
            "LEFT JOIN transaction t ON (DATE(t.created_at) BETWEEN STR_TO_DATE(d.start_date, '%Y/%m/%d') AND STR_TO_DATE(d.end_date, '%Y/%m/%d')) AND t.company_id = :companyId\n" +
            "GROUP BY d.week_id, d.week_num, d.start_date, d.end_date\n" +
            "ORDER BY d.week_num", nativeQuery = true)
    List<Object[]> getRecentTransactionsLast4Weeks(@Param("companyId") Long companyId);

    @Query(value = "WITH RECURSIVE date_ranges AS (\n" +
            "    SELECT DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 MONTH), '%Y-%m') AS month_id,\n" +
            "           DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 MONTH), '%Y-%m-01') AS start_date,\n" +
            "           LAST_DAY(DATE_SUB(CURRENT_DATE, INTERVAL 2 MONTH)) AS end_date,\n" +
            "           1 AS month_num\n" +
            "    UNION ALL\n" +
            "    SELECT DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH), '%Y-%m'),\n" +
            "           DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH), '%Y-%m-01'),\n" +
            "           LAST_DAY(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH)),\n" +
            "           month_num + 1\n" +
            "    FROM date_ranges\n" +
            "    WHERE month_num < 3\n" +
            ")\n" +
            "SELECT NULL AS days,\n" +
            "       NULL AS weeks,\n" +
            "       d.month_id AS months,\n" +
            "       COALESCE(COUNT(t.id), 0) AS count,\n" +
            "       DATE_FORMAT(STR_TO_DATE(d.month_id, '%Y-%m'), '%b') AS WeekLabel\n" +
            "FROM date_ranges d\n" +
            "LEFT JOIN transaction t ON (DATE(t.created_at) BETWEEN d.start_date AND d.end_date AND t.company_id = :companyId\n" +
            "GROUP BY d.month_id, d.month_num, d.start_date, d.end_date\n" +
            "ORDER BY d.month_num", nativeQuery = true)
    List<Object[]> getRecentTransactionsLast3Months(@Param("companyId") Long companyId);

    @Query(value = "WITH RECURSIVE date_ranges AS (\n" +
            "    SELECT DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 MONTH), '%Y-%m') AS month_id,\n" +
            "           DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 MONTH), '%Y-%m-01') AS start_date,\n" +
            "           LAST_DAY(DATE_SUB(CURRENT_DATE, INTERVAL 11 MONTH)) AS end_date,\n" +
            "           1 AS month_num\n" +
            "    UNION ALL\n" +
            "    SELECT DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH), '%Y-%m'),\n" +
            "           DATE_FORMAT(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH), '%Y-%m-01'),\n" +
            "           LAST_DAY(DATE_ADD(STR_TO_DATE(start_date, '%Y-%m-%d'), INTERVAL 1 MONTH)),\n" +
            "           month_num + 1\n" +
            "    FROM date_ranges\n" +
            "    WHERE month_num < 12\n" +
            ")\n" +
            "SELECT NULL AS days,\n" +
            "       NULL AS weeks,\n" +
            "       d.month_id AS months,\n" +
            "       COALESCE(COUNT(t.id), 0) AS count,\n" +
            "       DATE_FORMAT(STR_TO_DATE(d.month_id, '%Y-%m'), '%b') AS WeekLabel\n" +
            "FROM date_ranges d\n" +
            "LEFT JOIN transaction t ON (DATE(t.created_at) BETWEEN d.start_date AND d.end_date)\n" +
            "GROUP BY d.month_id, d.month_num, d.start_date, d.end_date AND t.company_id = :companyId\n" +
            "ORDER BY d.month_num", nativeQuery = true)
    List<Object[]> getRecentTransactionsLast12Months(@Param("companyId") Long companyId);
    Page<Transaction> findByCompany(Company company, Pageable pageable);
    @Query(value = "SELECT t FROM Transaction t WHERE t.company = :company")
    Page<Transaction> PaginatedTransactionsWithCompany(Pageable pageable, Company company);
}
