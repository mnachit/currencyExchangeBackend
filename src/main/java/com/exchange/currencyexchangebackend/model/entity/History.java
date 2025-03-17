package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.TypeHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    private Date dateCreated;
    private Date dateDeleted;
    private String fromCurrency;
    private String toCurrency;
    @Enumerated(EnumType.STRING)
    private TypeHistory typeHistory;
    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
