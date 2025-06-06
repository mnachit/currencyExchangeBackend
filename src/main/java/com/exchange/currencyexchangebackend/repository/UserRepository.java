package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.dto.UserRegisterRequest;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //findByEmail
    Optional<User> findByEmail(String email);
    @Query("SELECT u.company FROM User u WHERE u.id = :userId")
    Optional<Company> getCompanyByUserId(Long userId);
    //getAllUsersWithCompanyNotIdUser
    @Query("SELECT u FROM User u WHERE u.company = :company AND u.id != :userId")
    List<User> getAllUsersWithCompanyNotIdUser(Company company, Long userId);

    List<User> findByCompany(Company company);
    List<User> findAllByCompany(Company company);

}
