package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.config.GoogleAuthorizationConfig;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

    @Value("${spreadsheet.id}")
    private String spreadsheetId;

    @Autowired
    private GoogleAuthorizationConfig googleAuthorizationConfig;

    @Override
    public void getSpreadsheetValues() throws IOException, GeneralSecurityException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
        Sheets.Spreadsheets.Values.BatchGet request =
                sheetsService.spreadsheets().values().batchGet(spreadsheetId);
        request.setRanges(getSpreadSheetRange());
        request.setMajorDimension("ROWS");
        BatchGetValuesResponse response = request.execute();
        List<List<Object>> spreadSheetValues = response.getValueRanges().get(0).getValues();
        List<Object> headers = spreadSheetValues.remove(0);
        for (List<Object> row : spreadSheetValues) {
            String dateStr = row.get(0).toString(); // Assuming the date is in the first column
            String month = "sss";
            LOGGER.info("Month: {}", month);
        }
    }

    private List<String> getSpreadSheetRange() throws IOException, GeneralSecurityException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
        Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
        Spreadsheet spreadsheet = request.execute();
        Sheet sheet = spreadsheet.getSheets().get(0);
        int row = sheet.getProperties().getGridProperties().getRowCount();
        int col = sheet.getProperties().getGridProperties().getColumnCount();
        return Collections.singletonList("R1C1:R".concat(String.valueOf(row))
                .concat("C").concat(String.valueOf(col)));
    }
}
