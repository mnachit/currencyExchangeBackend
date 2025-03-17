package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.RecentReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentReportsRepository extends JpaRepository<RecentReports, Long> {
    public List<RecentReports> findTop5ByCompanyOrderByCreatedAtDesc(Company company);
}
