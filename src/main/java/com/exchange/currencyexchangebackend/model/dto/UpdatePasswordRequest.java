package com.exchange.currencyexchangebackend.model.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String twoFactorEnabled;
}
