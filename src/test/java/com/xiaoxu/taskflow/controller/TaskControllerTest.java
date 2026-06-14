package com.xiaoxu.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxu.taskflow.dto.TaskRequestDTO;
import com.xiaoxu.taskflow.dto.TaskResponseDTO;
import com.xiaoxu.taskflow.entity.Task;
import com.xiaoxu.taskflow.exception.GlobalExceptionHandler;
import com.xiaoxu.taskflow.security.JwtAuthFilter;
import com.xiaoxu.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.xiaoxu.taskflow.entity.TaskStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService service;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private GlobalExceptionHandler.JwtAuthEntryPoint jwtAuthEntryPoint;

    @Test
    void shouldGetTaskById() throws Exception {

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(1L);
        dto.setTitle("Test Task");

        when(service.getTaskById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title")
                        .value("Test Task"));
    }

    @Test
    void shouldCreateTask() throws Exception {

        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setStatus(TaskStatus.IN_PROGRESS);

        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(1L);
        response.setTitle("New Task");

        when(service.createTask(any(TaskRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Task created successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(service).createTask(any(TaskRequestDTO.class));
    }

    @Test
    void shouldUpdateTask() throws Exception {

        Task task = new Task();
        task.setTitle("Updated");

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(1L);
        dto.setTitle("Updated");

        when(service.updateTask(eq(1L), any(Task.class)))
                .thenReturn(dto);

        mockMvc.perform(
                        put("/api/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(task))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Task updated successfully"))
                .andExpect(jsonPath("$.data.title")
                        .value("Updated"));
    }

    @Test
    void shouldDeleteTask() throws Exception {

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Task deleted successfully"));

        verify(service).deleteTask(1L);
    }
}