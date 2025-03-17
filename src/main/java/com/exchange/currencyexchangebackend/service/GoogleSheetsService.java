package com.exchange.currencyexchangebackend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public interface GoogleSheetsService {
    public void getSpreadsheetValues() throws IOException, GeneralSecurityException;
}
