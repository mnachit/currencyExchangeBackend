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
//    @NotBlank(message = "website is mandatory")
    private String website;
//    @NotBlank(message = "logoUrl is mandatory")
    private String logoUrl;
//    @NotBlank(message = "description is mandatory")
    private String description;
    @NotBlank(message = "city is mandatory")
    private String city;
    @NotBlank(message = "postalCode is mandatory")
    private String postalCode;
    @NotBlank(message = "street is mandatory")
    private String street;
//    @NotBlank(message = "houseNumber is mandatory")
    private String houseNumber;
}
