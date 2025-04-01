package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.model.dto.ReportsDto;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.RecentActivities;
import com.exchange.currencyexchangebackend.model.entity.RecentReports;
import com.exchange.currencyexchangebackend.repository.RecentActivitiesRespository;
import com.exchange.currencyexchangebackend.repository.RecentReportsRepository;
import com.exchange.currencyexchangebackend.service.RecentReportsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Data
public class RecentReportsServiceImpl implements RecentReportsService {
    private final RecentReportsRepository recentReportsRepository;
    private final RecentActivitiesRespository recentActivitiesRespository;
    @Override
    public List<RecentReports> getRecent5Reports(Company company) {
        return recentReportsRepository.findTop5ByCompanyOrderByCreatedAtDesc(company);
    }

    @Override
    public List<RecentActivities> recentReportsWithType(Company company, String kind) {
        return recentActivitiesRespository.findTop5ByCompanyAndKindOrderByIdDesc(company, kind);
    }

    public RecentReports TorecentReports(ReportsDto reportsDto, Company company, String reportName) {
        RecentReports recentReports = new RecentReports();
        recentReports.setReportName(reportName);
        recentReports.setFormat(reportsDto.getFormat());
        recentReports.setStartDate(reportsDto.getStartDate());
        recentReports.setEndDate(reportsDto.getEndDate());
        recentReports.setCurrency(reportsDto.getCurrency());
        recentReports.setStatus(reportsDto.getStatus());
        recentReports.setCreatedAt(new Date());
        recentReports.setCompany(company);
        recentReports.setReportType(reportsDto.getReportType());
        return recentReportsRepository.save(recentReports);
    }

    @Override
    public boolean saveRecentActivities(RecentActivities recentActivities, Company company) {
        recentActivities.setTime(new Date());
        recentActivities.setCompany(company);
        recentActivitiesRespository.save(recentActivities);
        return true;
    }
}
