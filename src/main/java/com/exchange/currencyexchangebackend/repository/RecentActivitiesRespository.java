package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.RecentActivities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentActivitiesRespository extends JpaRepository<RecentActivities, Long> {
    List<RecentActivities> findTop5ByCompanyAndKindOrderByIdDesc(Company company, String kind);
}