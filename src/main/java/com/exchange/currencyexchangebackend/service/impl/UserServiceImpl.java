package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.dto.*;
import com.exchange.currencyexchangebackend.model.entity.*;
import com.exchange.currencyexchangebackend.model.enums.NamePermission;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import com.exchange.currencyexchangebackend.model.mapper.ProfileMapper;
import com.exchange.currencyexchangebackend.model.mapper.UserMapper;
import com.exchange.currencyexchangebackend.repository.CompanyRepository;
import com.exchange.currencyexchangebackend.repository.PermissionsRepository;
import com.exchange.currencyexchangebackend.repository.UserRepository;
import com.exchange.currencyexchangebackend.service.RecentReportsService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PermissionsRepository permissionsRepository;
    private final RecentReportsService recentReportsService;

    private final BCryptPasswordEncoder passwordEncoder;

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    @Override
    public boolean findByEmailAndPassword(String email, String password) throws ValidationException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        List<ErrorMessage> errorMessages1 = new ArrayList<>();
        if (userOptional.isEmpty())
            errorMessages1.add(ErrorMessage.builder().message("Email is incorrect").build());

        User user = userOptional.get();
        if (isPasswordValid(password, user.getPassword()) == false)
            errorMessages1.add(ErrorMessage.builder().message("Password is incorrect").build());
        if (errorMessages1.size() > 0)
            throw new ValidationException(errorMessages1);
        return true;
    }

    @Override
    public User findByID(Long id) throws ValidationException {
        if (userRepository.findById(id).isEmpty()) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("User not found").build()));
        }
        return userRepository.findById(id).get();
    }

    @Override
    public Long findIdByEmail(String email) throws ValidationException {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Email not found").build()));
        }
        return userRepository.findByEmail(email).get().getId();
    }

    @Override
    public User findByEmail(String email) throws ValidationException{
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Email not found").build()));
        }

        return userRepository.findByEmail(email).get();
    }

    @Override
    public User saveUserRoleAdmin(User user) throws ValidationException {
//        user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            errorMessages.add(ErrorMessage.builder().message("Email already exists").build());
        if (errorMessages.size() > 0)
            throw new ValidationException(errorMessages);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

//        user.setRoleUser(RoleUser.USER);
        Date date = new Date();
        user.setCreatedAt(date);
        userRepository.save(user);
        return user;
    }

    @Override
    public boolean isEmailExist(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveUser(User user, List<NamePermission> namePermission, RoleUser roleUser, Long companyId) throws ValidationException {
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            errorMessages.add(ErrorMessage.builder().message("Email already exists").build());
        if (errorMessages.size() > 0)
            throw new ValidationException(errorMessages);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleUser);
        user.setCompany(companyRepository.findById(companyId).isPresent() ? companyRepository.findById(companyId).get() : null);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setActive(true);
        user.setLocked(false);
        userRepository.save(user);
        Permissions permissions = new Permissions();
        for (NamePermission namePermission1 : namePermission) {
            permissions.setName(namePermission1);
            permissions.setUser(user);
            permissionsRepository.save(permissions);
        }
        return true;
    }

    @Override
    public boolean checkRoleUser(Long userId, NamePermission namePermission) throws ValidationException{
        User user = userRepository.findById(userId).isPresent() ? userRepository.findById(userId).get() : null;
        List<ErrorMessage> errorMessages = new ArrayList<>();
        if (user.getRole().equals(RoleUser.MANAGER)) {
            return true;
        }
        if (user.getRole().equals(RoleUser.ADMIN) && permissionsRepository.findByNameAndUser(namePermission, user)) {
            return true;
        }
        errorMessages.add(ErrorMessage.builder().message("Permission denied").build());
        throw new ValidationException(errorMessages);
    }

    @Override
    public Company getCompanyByUserId(Long userId) {
        return userRepository.getCompanyByUserId(userId).get();
    }

    @Override
    public User getUserById(Long userId) throws ValidationException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("User not found").build()));
        }
        return userRepository.findById(userId).get();
    }

    @Override
    public List<UserDto> getAllUsersWithCompanyNotIdUser(Company company, Long userId) {
        return UserMapper.toUserDtoList(userRepository.getAllUsersWithCompanyNotIdUser(company, userId));
    }

    @Override
    public UserRegisterRequest saveNewUser(UserRegisterRequest userRegisterRequest, Company company) throws ValidationException {
        if (userRegisterRequest.getId() != null) {
            User user = userRepository.findById(userRegisterRequest.getId()).get();
            if (userRegisterRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
            }
            else {
                user.setPassword(user.getPassword());
            }
            user.setFullName(userRegisterRequest.getFullName());
            user.setEmail(userRegisterRequest.getEmail());
            user.setPhoneNumber(userRegisterRequest.getPhoneNumber());
            user.setAddress(userRegisterRequest.getAddress());
            user.setUpdatedAt(new Date());
            if (userRegisterRequest.getStatus() == true) {
                user.setActive(true);
                user.setLocked(false);
            }
            else
            {
                user.setActive(false);
                user.setLocked(true);
            }
            user.setNotes(userRegisterRequest.getNotes());
            user.setRole(userRegisterRequest.getRole());
            userRepository.save(user);
            return userRegisterRequest;
        }
        else
        {
            if (userRepository.findByEmail(userRegisterRequest.getEmail()).isPresent()) {
                throw new ValidationException(List.of(ErrorMessage.builder().message("Email already exists").build()));
            }
            userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
            userRegisterRequest.setCompany(company);
            if (userRegisterRequest.getStatus() == true) {
                userRegisterRequest.setActive(true);
                userRegisterRequest.setLocked(false);
            }
            else
            {
                userRegisterRequest.setActive(false);
                userRegisterRequest.setLocked(true);
            }
            UserRegisterRequest registerRequest = UserMapper.userToUserRegisterRequest(userRepository.save(UserMapper.userRegsiterRequestToUser(userRegisterRequest)));
            RecentActivities recentActivities = new RecentActivities();
            recentActivities.setAction("User Created");
            recentActivities.setDescription("New user "+registerRequest.getFullName()+" was created");
            recentActivities.setType("success");
            recentActivities.setCreatedAt(new Date());
            recentActivities.setKind("User");
            recentActivities.setIcon("fa-user-plus");
            recentReportsService.saveRecentActivities(recentActivities, company);
            return registerRequest;
        }
    }

    @Override
    public Boolean deleteUser(Long id, Long idUser, Company company) throws ValidationException {
        if (userRepository.findById(id).get().getRole().equals(RoleUser.MANAGER)) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Permission denied").build()));
        }
        if (userRepository.findById(idUser).isPresent()) {
            RecentActivities recentActivities = new RecentActivities();
            recentActivities.setAction("User Deleted");
            recentActivities.setDescription(userRepository.findById(idUser).get().getFullName()+" was removed");
            recentActivities.setCreatedBy(userRepository.findById(idUser).get());
            recentActivities.setType("danger");
            recentActivities.setCreatedAt(new Date());
            recentActivities.setKind("User");
            recentActivities.setIcon("fa-user-times");
            recentReportsService.saveRecentActivities(recentActivities, company);
            userRepository.deleteById(id);
            return true;
        }
        throw new ValidationException(List.of(ErrorMessage.builder().message("User not found").build()));
    }

    @Override
    public Boolean changeStatus(List<Long> id, Long idUser, String role, Company company) throws ValidationException {
        // First check if manager has permission
        User manage = userRepository.findById(idUser)
                .orElseThrow(() -> new ValidationException(List.of(ErrorMessage.builder().message("User not found").build())));

        if (manage.getRole().equals(RoleUser.MANAGER.name())) {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Permission denied").build()));
        }

        // Create a single activity record for the batch operation
        RecentActivities batchActivity = new RecentActivities();
        batchActivity.setCreatedBy(manage);
        batchActivity.setCreatedAt(new Date());
        batchActivity.setKind("User");

        if (role.equals("active")) {
            batchActivity.setAction("Bulk Activate");
            batchActivity.setType("success");
            batchActivity.setIcon("fa-check-circle");
            batchActivity.setDescription(id.size() + " users were activated");
        } else if (role.equals("locked")) {
            batchActivity.setAction("Bulk Deactivate");
            batchActivity.setType("warning");
            batchActivity.setIcon("fa-ban");
            batchActivity.setDescription(id.size() + " users were deactivated");
        }

        // Save the batch activity
        recentReportsService.saveRecentActivities(batchActivity, company);

        // Process all IDs
        boolean allSuccessful = true;
        List<String> failedIds = new ArrayList<>();

        for (Long userId : id) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ValidationException(List.of(ErrorMessage.builder().message("User not found: " + userId).build())));

                if (role.equals("active")) {
                    user.setActive(true);
                    user.setLocked(false);
                } else if (role.equals("locked")) {
                    user.setActive(false);
                    user.setLocked(true);
                }

                // Save each user
                if (userRepository.save(user) == null) {
                    allSuccessful = false;
                    failedIds.add(userId.toString());
                }

            } catch (Exception e) {
                allSuccessful = false;
                failedIds.add(userId.toString());
            }
        }

        // If some users failed to update, log this information
        if (!allSuccessful) {
            // You could log the failed IDs here or return them to the caller
            System.err.println("Failed to update users with IDs: " + String.join(", ", failedIds));
        }

        return allSuccessful;
    }

    @Override
    public ProfileDto getMockUserSettings(Long id) {
        return ProfileMapper.toDto(userRepository.findById(id).get());
    }

    @Override
    public ProfileDto updateUser(ProfileDto profileDto, Long id) {
//        if (userRepository.findByEmail(profileDto.getEmail()).isPresent()) {
//            throw new ValidationException(List.of(ErrorMessage.builder().message("Email already exists").build()));
//        }
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            user.setFullName(profileDto.getFullName());
//            user.setEmail(profileDto.getEmail());
            user.setPhoneNumber(profileDto.getPhone());
            user.setAddress(profileDto.getAddress());
            userRepository.save(user);
            return ProfileMapper.toDto(user);
        }
        return null;
    }

    @Override
    public boolean update_password(UpdatePasswordRequest updatePasswordRequest, Long idUser) {
        User user = getUserById(idUser);
        if (passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            userRepository.save(user);
            return true;
        } else {
            throw new ValidationException(List.of(ErrorMessage.builder().message("Password is incorrect").build()));
        }
    }

    public ReportsDto setReportsDto(String name)
    {
        ReportsDto reportsDto = new ReportsDto();
        reportsDto.setReportType("User");
        reportsDto.setStatus(name);
        return reportsDto;
    }


}
