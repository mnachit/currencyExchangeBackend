package com.exchange.currencyexchangebackend.model.mapper;

import com.exchange.currencyexchangebackend.model.dto.UserDto;
import com.exchange.currencyexchangebackend.model.dto.UserRegisterRequest;
import com.exchange.currencyexchangebackend.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .locked(user.isLocked())
                .createdAt(user.getCreatedAt())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .notes(user.getNotes())
                .build();
    }

    public static List<UserDto> toUserDtos(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    public static User userRegsiterRequestToUser(UserRegisterRequest userRegisterRequest) {
        return User.builder()
                .fullName(userRegisterRequest.getFullName())
                .email(userRegisterRequest.getEmail())
                .password(userRegisterRequest.getPassword())
                .role(userRegisterRequest.getRole())
                .phoneNumber(userRegisterRequest.getPhoneNumber())
                .address(userRegisterRequest.getAddress())
                .notes(userRegisterRequest.getNotes())
                .createdAt(userRegisterRequest.getCreatedAt())
                .updatedAt(userRegisterRequest.getUpdatedAt())
                .company(userRegisterRequest.getCompany())
                .active(userRegisterRequest.isActive())
                .locked(userRegisterRequest.isLocked())
                .build();
    }

    public static UserRegisterRequest userToUserRegisterRequest(User user) {
        return UserRegisterRequest.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .notes(user.getNotes())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .company(user.getCompany())
                .active(user.isActive())
                .locked(user.isLocked())
                .build();
    }
}
