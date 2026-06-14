package com.xiaoxu.taskflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxu.taskflow.dto.LoginRequest;
import com.xiaoxu.taskflow.dto.RegisterRequest;
import com.xiaoxu.taskflow.dto.TaskRequestDTO;
import com.xiaoxu.taskflow.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateAndGetTask() throws Exception {

        // Register user

        String username =
                "john_" + System.currentTimeMillis();

        RegisterRequest registerRequest =
                new RegisterRequest();

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

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setUsername(username);
        loginRequest.setPassword("123456");


        String response =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(loginRequest)
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        String accessToken =
                objectMapper
                        .readTree(response)
                        .path("data")
                        .path("accessToken")
                        .asText();


        // Create Task

        TaskRequestDTO request =
                new TaskRequestDTO();

        request.setTitle("My Task");
        request.setDescription("My Description");
        request.setStatus(TaskStatus.TODO);


        mockMvc.perform(
                        post("/api/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.title")
                        .value("My Task"));


        // Get all tasks

        mockMvc.perform(
                        get("/api/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.content[0].title")
                        .value("My Task"));

    }


    @Test
    void shouldDeleteTask() throws Exception {

        // Register

        String username =
                "john_" + System.currentTimeMillis();

        RegisterRequest registerRequest =
                new RegisterRequest();

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

        LoginRequest loginRequest =
                new LoginRequest();

        loginRequest.setUsername(username);
        loginRequest.setPassword("123456");


        String loginResponse =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(loginRequest)
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        String token =
                objectMapper
                        .readTree(loginResponse)
                        .path("data")
                        .path("accessToken")
                        .asText();


        // Create task

        TaskRequestDTO request =
                new TaskRequestDTO();

        request.setTitle("Delete Me");
        request.setDescription("desc");
        request.setStatus(TaskStatus.TODO);


        String createResponse =
                mockMvc.perform(
                                post("/api/tasks")
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(request)
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        Long taskId =
                objectMapper
                        .readTree(createResponse)
                        .path("data")
                        .path("id")
                        .asLong();


        // Delete

        mockMvc.perform(
                        delete("/api/tasks/" + taskId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value("Task deleted successfully"));

    }

}