package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.AuthResponse;
import com.finanzmanager.finanzapp.dto.LoginRequest;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.UserRepository;
import com.finanzmanager.finanzapp.service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getUsername()
        );
    }
}