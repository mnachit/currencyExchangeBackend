package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.FundBalanceDto;
import com.exchange.currencyexchangebackend.model.entity.FundBalance;

import java.util.List;

public class FundBalanceMapper {

    public static FundBalanceDto toDto(FundBalance fundBalance) {
        return FundBalanceDto.builder()
                .operationFunds(fundBalance.getOperationFunds())
                .currency(fundBalance.getCurrency())
                .amount(fundBalance.getAmount())
                .notes(fundBalance.getNotes())
                .updateBy(fundBalance.getUpdateBy())
                .createdAt(fundBalance.getCreatedAt())
                .updatedAt(fundBalance.getUpdatedAt())
                .code(fundBalance.getCode())
                .build();
    }

    public static FundBalance toEntity(FundBalanceDto fundBalanceDto) {
        FundBalance fundBalance = new FundBalance();
        fundBalance.setOperationFunds(fundBalanceDto.getOperationFunds());
        fundBalance.setCurrency(fundBalanceDto.getCurrency());
        fundBalance.setAmount(fundBalanceDto.getAmount());
        fundBalance.setNotes(fundBalanceDto.getNotes());
        fundBalance.setCode(fundBalanceDto.getCode());
        fundBalance.setCreatedAt(fundBalanceDto.getCreatedAt());
        fundBalance.setUpdatedAt(fundBalanceDto.getUpdatedAt());
        return fundBalance;
    }

    public static List<FundBalanceDto> toDtos(List<FundBalance> fundBalances) {
        return fundBalances.stream().map(FundBalanceMapper::toDto).toList();
    }

    public static List<FundBalance> toEntities(List<FundBalanceDto> fundBalanceDtos) {
        return fundBalanceDtos.stream().map(FundBalanceMapper::toEntity).toList();
    }
}
