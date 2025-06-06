package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.RecentActivities;
import com.exchange.currencyexchangebackend.model.entity.RecentReports;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecentReportsService {
    public List<RecentReports> getRecent5Reports(Company company);
    public List<RecentActivities> recentReportsWithType(Company company, String kind);
    public RecentReports TorecentReports(ReportsDto reportsDto, Company company, String reportName);
    public boolean saveRecentActivities(RecentActivities recentActivities, Company company);
}
