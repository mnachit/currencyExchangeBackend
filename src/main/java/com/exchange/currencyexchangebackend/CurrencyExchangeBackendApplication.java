package com.exchange.currencyexchangebackend;

import com.exchange.currencyexchangebackend.model.entity.User;
import com.exchange.currencyexchangebackend.model.enums.RoleUser;
import com.exchange.currencyexchangebackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@AllArgsConstructor
public class CurrencyExchangeBackendApplication {

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public void createUserWithRoleNachita() {
        if (userRepository.findByEmail("nachitmed70@gmail.com").isEmpty()) {
            User user = new User();
            user.setEmail("nachitmed70@gmail.com");
            user.setPassword(passwordEncoder.encode("Simonachit@2001++"));
            user.setRole(RoleUser.NACHIT);
            user.setFullName("Nachit Med"); // Ensure full_name is set
            userRepository.save(user);
        } else {
            System.out.println("User already exists");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(CurrencyExchangeBackendApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        createUserWithRoleNachita();
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };

    }
}
