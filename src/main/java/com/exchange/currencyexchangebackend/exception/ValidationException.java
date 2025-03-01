package com.exchange.currencyexchangebackend.exception;

import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Validated
public class ValidationException extends RuntimeException {
    private List<ErrorMessage> errorMessages;

    public ValidationException(List<ErrorMessage> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }
}
