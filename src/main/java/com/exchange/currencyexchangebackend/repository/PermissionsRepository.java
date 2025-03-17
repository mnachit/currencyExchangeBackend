package com.exchange.currencyexchangebackend.repository;

import com.exchange.currencyexchangebackend.model.entity.Permissions;
import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.NamePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    public boolean findByNameAndUser(NamePermission namePermission, User user);
}
