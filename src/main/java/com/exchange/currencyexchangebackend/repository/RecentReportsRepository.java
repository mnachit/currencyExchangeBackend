package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.RecentReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentReportsRepository extends JpaRepository<RecentReports, Long> {
    @Query("SELECT r FROM RecentReports r WHERE r.company = :company AND r.reportType != 'User' ORDER BY r.createdAt DESC")
    public List<RecentReports> findTop5ByCompanyOrderByCreatedAtDesc(Company company);
    @Query("SELECT r FROM RecentReports r WHERE r.company = :company AND r.reportType = :reportType ORDER BY r.createdAt DESC")
    public List<RecentReports> findTop5ByReportTypeCompanyOrderByCreatedAtDesc(Company company, String reportType);
}
