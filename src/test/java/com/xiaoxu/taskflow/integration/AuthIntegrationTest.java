package com.xiaoxu.taskflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser() throws Exception {

        RegisterRequest request = new RegisterRequest();

        String username = "john_" + System.currentTimeMillis();
        request.setUsername(username);
        request.setPassword("123456");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true));
    }


    @Test
    void shouldLoginSuccessfully() throws Exception {

        // Register user

        RegisterRequest registerRequest = new RegisterRequest();

        String username = "john_" + System.currentTimeMillis();

        registerRequest.setUsername(username);
        registerRequest.setPassword("123456");


        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(registerRequest)
                                )
                )
                .andExpect(status().isOk());


        // Login

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUsername(username);
        loginRequest.setPassword("123456");


        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(loginRequest)
                                )
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.success")
                        .value(true))

                .andExpect(jsonPath("$.message")
                        .value("Login successful"))

                .andExpect(jsonPath("$.data.accessToken")
                        .exists())

                .andExpect(jsonPath("$.data.refreshToken")
                        .exists());

    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {

        RegisterRequest request = new RegisterRequest();

        String username = "john_" + System.currentTimeMillis();

        request.setUsername(username);
        request.setPassword("123456");

        // First registration
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        // Second registration with same username
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                .value("Username already exists"));
    }
}