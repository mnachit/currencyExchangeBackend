package com.exchange.currencyexchangebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;
    private String description;
    private String country;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String Status;
}
