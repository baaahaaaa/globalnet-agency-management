package com.example.gestiongnet.controller;

import com.example.gestiongnet.dto.AuthRequest;
import com.example.gestiongnet.dto.AuthResponse;
import com.example.gestiongnet.model.User;
import com.example.gestiongnet.repository.UserRepository;
import com.example.gestiongnet.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Connexion avec JWT")
public class AuthController {
    private final UserRepository users;
    private final JwtUtil jwt;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository users, JwtUtil jwt, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Authentification")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        User user = users.findByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new AuthResponse(jwt.generateToken(user.getUsername(), user.getRole()), user.getRole()));
    }
}
