package com.exchange.currencyexchangebackend.model.entity;

import com.exchange.currencyexchangebackend.model.enums.NamePermission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private NamePermission name;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
