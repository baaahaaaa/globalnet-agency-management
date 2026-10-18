package com.example.gestiongnet.config;

import com.example.gestiongnet.model.User;
import com.example.gestiongnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner bootstrapAdmin(UserRepository users, PasswordEncoder passwordEncoder,
              @Value("${APP_ADMIN_USERNAME:}") String username,
              @Value("${APP_ADMIN_PASSWORD:}") String password) {
        return args -> {
            if (username.isBlank() || password.isBlank()) return;
            if (password.length() < 12) {
                throw new IllegalArgumentException("APP_ADMIN_PASSWORD must have at least 12 characters");
            }
            if (users.findByUsername(username).isEmpty()) {
                users.save(new User(null, username, passwordEncoder.encode(password), "ADMIN"));
            }
        };
    }
}
