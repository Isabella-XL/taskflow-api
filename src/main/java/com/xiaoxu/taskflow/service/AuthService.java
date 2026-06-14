package com.xiaoxu.taskflow.service;

import com.xiaoxu.taskflow.dto.AuthResponse;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.dto.RegisterRequest;
import com.xiaoxu.taskflow.entity.RefreshToken;
import com.xiaoxu.taskflow.entity.Role;
import com.xiaoxu.taskflow.entity.User;
import com.xiaoxu.taskflow.exception.ResourceNotFoundException;
import com.xiaoxu.taskflow.repository.UserRepository;
import com.xiaoxu.taskflow.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,  RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole() );

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
}