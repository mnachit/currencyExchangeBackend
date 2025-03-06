package com.exchange.currencyexchangebackend.controller;

import com.exchange.currencyexchangebackend.config.JwtUtil;
import com.exchange.currencyexchangebackend.model.dto.ErrorRes;
import com.exchange.currencyexchangebackend.model.dto.UserLoginRequest;
import com.exchange.currencyexchangebackend.model.dto.UserRegisterRequest;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.service.UserService;
import com.exchange.currencyexchangebackend.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;


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
}
