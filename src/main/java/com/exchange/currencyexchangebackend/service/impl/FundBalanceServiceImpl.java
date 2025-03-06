package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.entity.FundBalance;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.Currency;
import com.exchange.currencyexchangebackend.model.enums.OperationFunds;
import com.exchange.currencyexchangebackend.model.mapper.FundBalanceMapper;
import com.exchange.currencyexchangebackend.repository.FundBalanceRepository;
import com.exchange.currencyexchangebackend.repository.UserRepository;
import com.exchange.currencyexchangebackend.service.FundBalanceService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FundBalanceServiceImpl implements FundBalanceService {
    private final FundBalanceRepository fundBalanceRepository;
    private final UserRepository userRepository;

    @Override
    public FundBalanceDto saveFundBalance(FundBalanceDto fundBalanceDto) {
        fundBalanceDto.setCreatedAt(new Date());
        fundBalanceDto.setUpdatedAt(new Date());
//        fundBalanceDto.setUpdateBy(1L);
        fundBalanceDto.setCode("FUND"+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
        FundBalance fundBalance = FundBalanceMapper.toEntity(fundBalanceDto);
        fundBalance.setUpdateBy(userRepository.findById(1L).get());
        return FundBalanceMapper.toDto(fundBalanceRepository.save(fundBalance));
    }

    @Override
    public List<FundBalanceDto> getFundBalanceList() {
        return FundBalanceMapper.toDtos(fundBalanceRepository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    public BigDecimal getAvailableBalanceWithCurrency(Long userId, Currency currency) {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        Optional<User> user = userRepository.findById(1L);
        if (!user.isPresent()) {
            errorMessages.add(new ErrorMessage("User not found", 404));
        }
        if (errorMessages.size() > 0) {
            throw new ValidationException(errorMessages);
        }
        BigDecimal addAmount = fundBalanceRepository.getAvailableBalanceWithCurrencyAndOperationFunds(user.get(), currency, OperationFunds.add);
        BigDecimal withdrawAmount = fundBalanceRepository.getAvailableBalanceWithCurrencyAndOperationFunds(user.get(), currency, OperationFunds.withdraw);
        return addAmount.subtract(withdrawAmount);
    }
}
