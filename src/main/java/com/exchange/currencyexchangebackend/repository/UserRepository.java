package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //findByEmail
    Optional<User> findByEmail(String email);
}
