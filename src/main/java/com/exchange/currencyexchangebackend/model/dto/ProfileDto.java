package com.exchange.currencyexchangebackend.model.dto;

import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private Long id;
    private String fullName;
    private String email;
    @Enumerated(EnumType.STRING)
    private RoleUser role;
    private String bio;
    private String phone;
    private String avatarUrl;
    private String address;
}
