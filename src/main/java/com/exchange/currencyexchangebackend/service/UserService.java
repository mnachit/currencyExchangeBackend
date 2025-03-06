package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public boolean findByEmailAndPassword(String email, String password) throws ValidationException;
    public User findByID(Long id) throws ValidationException;
    public Long findIdByEmail(String email) throws ValidationException;
    public User findByEmail(String email) throws ValidationException;
    public User saveUserRoleAdmin(User user) throws ValidationException;
}
