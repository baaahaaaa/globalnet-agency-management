package com.example.gestiongnet.dto;

public record AuthResponse(
        String token,
        String role
) {}