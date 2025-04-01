package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.ProfileDto;
import com.exchange.currencyexchangebackend.model.entity.User;
import lombok.Builder;

@Builder
public class ProfileMapper {
    public static ProfileDto toDto(User user) {
        return ProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .build();
    }
}