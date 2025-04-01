package com.exchange.currencyexchangebackend.model.entity;

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
public class RecentActivities {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private String action;
    private String description;
    private String type;
    private String icon;
    private Date time;
    private Date createdAt;
    private String kind;
}
