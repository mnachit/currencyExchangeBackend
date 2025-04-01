package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.*;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.service.TransactionService;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionService transactionService;


    @PostMapping("/rest/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        Response<String> response = new Response<>();
        try {
            userService.findByEmailAndPassword(userLoginRequest.getEmail(), userLoginRequest.getPassword());

            User user = userService.findByEmail(userLoginRequest.getEmail());
            String token = jwtUtil.createToken(user);
            response.setResult(token);
            response.setMessage("Logged in successfully");
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User has not been logged in");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/rest/auth/new/user")
    public ResponseEntity<?> createUser(@RequestBody User userLoginRequest)
    {
        Response<String> userDtoResponseResponse = new Response<>();
        try {
            User user = userLoginRequest;
            user.setEmail(userLoginRequest.getEmail());
            userService.saveUserRoleAdmin(user);
            user.setId(userService.findIdByEmail(user.getEmail()));
            String token = jwtUtil.createToken(user);
            userDtoResponseResponse.setMessage("User has been added");
            userDtoResponseResponse.setResult(token);
            return ResponseEntity.ok(userDtoResponseResponse);
        }catch (BadCredentialsException e){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST,"Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/user/getList")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        Response<List<UserDto>> response = new Response<>();
        try {
            Long idUser = JwtUtil.extractUserId(token);
            Company company = userService.getCompanyByUserId(idUser);
            List<UserDto> user = userService.getAllUsersWithCompanyNotIdUser(company, idUser);
            response.setResult(user);
            response.setMessage("User found");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not found");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/save")
    ResponseEntity<?> saveUser(@RequestBody UserRegisterRequest userRegisterRequest, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<UserRegisterRequest> response = new Response<>();
        try {
            response.setResult(userService.saveNewUser(userRegisterRequest, company));
            response.setMessage("User created successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not created");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UserRegisterRequest userRegisterRequest, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<UserRegisterRequest> response = new Response<>();
        try {
            response.setResult(userService.saveNewUser(userRegisterRequest, company));
            response.setMessage("User updated successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not updated");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<String> response = new Response<>();
        try {
            userService.deleteUser(id, idUser, company);
            response.setMessage("User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not deleted");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/changeStatus/{role}")
    public ResponseEntity<?> changeStatus(@RequestBody List<Long> id, @PathVariable String role, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<String> response = new Response<>();
        try {
            userService.changeStatus(id, idUser, role, company);
            response.setMessage("Role changed successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("Role not changed");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user/getMockUserSettings")
    public ResponseEntity<?> getMockUserSettings(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<ProfileDto> response = new Response<>();
        try {
            response.setResult(userService.getMockUserSettings(idUser));
            response.setMessage("User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not deleted");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody ProfileDto user, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<ProfileDto> response = new Response<>();
        try {
            response.setResult(userService.updateUser(user, idUser));
            response.setMessage("User updated successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("User not updated");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, @RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Response<String> response = new Response<>();
        try {
            userService.update_password(updatePasswordRequest, idUser);
            response.setMessage("Password updated successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("Password not updated");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user/export/transactions/excel")
    public ResponseEntity<?> exportExcel(@RequestHeader("Authorization") String token) {
        Long idUser = JwtUtil.extractUserId(token);
        Company company = userService.getCompanyByUserId(idUser);
        Response<ResponseEntity<ByteArrayResource>> response = new Response<>();
        try {
            response.setResult(transactionService.exportAllExcel(company));
            response.setMessage("Transactions exported successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.validation.ValidationException e) {
            response.setMessage("Transactions not exported");
            return ResponseEntity.ok(response);
        }
    }

}
