package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RecentReports {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    private String reportName;private String format;
    private Date startDate;
    private Date endDate;
    private Currency currency;
    private String status;
    private Date createdAt;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private String reportType;
}
