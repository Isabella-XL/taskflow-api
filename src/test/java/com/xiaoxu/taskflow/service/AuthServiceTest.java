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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("password123");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("john", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("password123");

        User existingUser = new User();
        existingUser.setUsername("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(existingUser));

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("john", Role.USER))
                .thenReturn("access-token");

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(jwtService).generateToken("john", Role.USER);
        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(any(), any());
    }

    @Test
    void shouldThrowWhenPasswordIsInvalid() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrong-password");

        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encodedPassword"))
                .thenReturn(false);

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(any(), any());
    }
}