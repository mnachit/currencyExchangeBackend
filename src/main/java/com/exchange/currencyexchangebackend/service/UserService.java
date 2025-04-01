package com.exchange.currencyexchangebackend.service;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.ProfileDto;
import com.exchange.currencyexchangebackend.model.dto.UpdatePasswordRequest;
import com.exchange.currencyexchangebackend.model.dto.UserDto;
import com.exchange.currencyexchangebackend.model.dto.UserRegisterRequest;
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
    public List<UserDto> getAllUsersWithCompanyNotIdUser(Company company, Long userId);
    public UserRegisterRequest saveNewUser(UserRegisterRequest userRegisterRequest, Company company) throws ValidationException;
    public Boolean deleteUser(Long id, Long idUser, Company company) throws ValidationException;
    public Boolean changeStatus(List<Long> id, Long idUser, String role, Company company) throws ValidationException;
    public ProfileDto getMockUserSettings(Long id);
    public ProfileDto updateUser(ProfileDto profileDto, Long id);

    public boolean update_password(UpdatePasswordRequest updatePasswordRequest, Long idUser);
}
