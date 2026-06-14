package com.xiaoxu.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxu.taskflow.dto.AuthResponse;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.dto.RefreshTokenRequest;
import com.xiaoxu.taskflow.dto.RegisterRequest;
import com.xiaoxu.taskflow.entity.RefreshToken;
import com.xiaoxu.taskflow.entity.Role;
import com.xiaoxu.taskflow.entity.User;
import com.xiaoxu.taskflow.security.JwtService;
import com.xiaoxu.taskflow.service.AuthService;
import com.xiaoxu.taskflow.service.RefreshTokenService;
import com.xiaoxu.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private TaskService service;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUser() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("password123");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        AuthResponse authResponse =
                new AuthResponse(
                        "access-token",
                        "refresh-token"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken")
                        .value("refresh-token"));
    }

    @Test
    void shouldRefreshToken() throws Exception {

        User user = new User();
        user.setUsername("john");
        user.setRole(Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setUser(user);

        when(refreshTokenService.verifyExpiration("refresh-token"))
                .thenReturn(refreshToken);

        when(jwtService.generateToken("john", Role.USER))
                .thenReturn("new-access-token");

        RefreshTokenRequest request =
                new RefreshTokenRequest();

        request.setRefreshToken("refresh-token");

        mockMvc.perform(
                        post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"));
    }


}