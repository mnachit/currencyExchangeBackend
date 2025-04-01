package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    @Enumerated(EnumType.STRING)
    private RoleUser role;
    private boolean active;
    private boolean locked;
    private Date createdAt;
    private String phoneNumber;
    private String address;
    private String notes;
}
