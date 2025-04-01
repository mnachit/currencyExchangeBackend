package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Builder
public class UserRegisterRequest {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private RoleUser role;
    private Boolean status;
    private String phoneNumber;
    private String address;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    private Company company;
    private boolean active;
    private boolean locked;
}
