package com.exchange.currencyexchangebackend.service.impl;

import com.exchange.currencyexchangebackend.exception.ValidationException;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.repository.UserRepository;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.ErrorMessage;
import lombok.AllArgsConstructor;
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
}
