package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.NamePermission;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    public boolean findByEmailAndPassword(String email, String password) throws ValidationException;
    public User findByID(Long id) throws ValidationException;
    public Long findIdByEmail(String email) throws ValidationException;
    public User findByEmail(String email) throws ValidationException;
    public User saveUserRoleAdmin(User user) throws ValidationException;
    public boolean isEmailExist(String email);
    public boolean saveUser(User user, List<NamePermission> namePermission, RoleUser roleUser, Long companyId) throws ValidationException;
    public boolean checkRoleUser(Long userId, NamePermission namePermission) throws ValidationException;
    public Company getCompanyByUserId(Long userId);
    public User getUserById(Long userId) throws ValidationException;
}
