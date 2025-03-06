package com.exchange.currencyexchangebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class ErrorRes {
    HttpStatus httpStatus;
    String message;
}
