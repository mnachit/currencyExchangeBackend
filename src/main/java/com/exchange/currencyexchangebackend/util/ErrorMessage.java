package com.exchange.currencyexchangebackend.util;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private String message;
    private int status;

//    public ErrorMessage(String message) {
//        this.message = message;
//    }
}
