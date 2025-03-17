package com.exchange.currencyexchangebackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CompanyDto {
    @NotBlank(message = "name is mandatory")
    private String name;
    @NotBlank(message = "address is mandatory")
    private String address;
    @NotBlank(message = "phone is mandatory")
    private String phone;
    @NotBlank(message = "email is mandatory")
    private String email;
    private String website;
    private String logoUrl;
    private String description;
    @NotBlank(message = "city is mandatory")
    private String city;
    @NotBlank(message = "postalCode is mandatory")
    private String postalCode;
    @NotBlank(message = "street is mandatory")
    private String street;
    private String houseNumber;
    private String nameManager;
    private String emailManager;
    private String passwordManager;
}
