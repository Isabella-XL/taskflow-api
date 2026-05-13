package com.xiaoxu.taskflow.controller;

import com.xiaoxu.taskflow.dto.AuthResponse;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        // temporary fake login
        // later we connect database

        if ("admin".equals(request.getUsername())
                && "1234".equals(request.getPassword())) {

            String token =
                    jwtService.generateToken(request.getUsername());

            return new AuthResponse(token);
        }

        throw new RuntimeException("Invalid username or password");
    }
}