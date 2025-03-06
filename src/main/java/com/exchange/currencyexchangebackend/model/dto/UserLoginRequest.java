package com.exchange.currencyexchangebackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "Email Or Username cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
