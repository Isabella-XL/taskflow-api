package com.xiaoxu.taskflow.controller;

import com.xiaoxu.taskflow.dto.AuthResponse;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.dto.RefreshTokenRequest;
import com.xiaoxu.taskflow.dto.RegisterRequest;
import com.xiaoxu.taskflow.entity.RefreshToken;
import com.xiaoxu.taskflow.entity.User;
import com.xiaoxu.taskflow.response.ApiResponse;
import com.xiaoxu.taskflow.security.JwtService;
import com.xiaoxu.taskflow.service.AuthService;
import com.xiaoxu.taskflow.service.RefreshTokenService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;
    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          JwtService jwtService) {

        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return new ApiResponse<>(
                true,
                "User registered successfully",
                null
        );
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        RefreshToken refreshToken =
                refreshTokenService.verifyExpiration(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
}