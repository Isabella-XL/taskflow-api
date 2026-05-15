package com.xiaoxu.taskflow.controller;

import com.xiaoxu.taskflow.entity.Task;
import com.xiaoxu.taskflow.response.ApiResponse;
import com.xiaoxu.taskflow.service.TaskService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.xiaoxu.taskflow.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // GET /api/tasks
    @GetMapping
    public ApiResponse<List<TaskResponseDTO>> getTasks() {

        List<TaskResponseDTO> tasks =
                service.getAllTasks();

        return new ApiResponse<>(
                true,
                "Tasks fetched successfully",
                tasks
        );
    }

    // GET by id
    @GetMapping("/{id}")
    public ApiResponse<TaskResponseDTO> getTaskById(@PathVariable Long id) {

        TaskResponseDTO task = service.getTaskById(id);

        return new ApiResponse<>(
                true,
                "Task fetched successfully",
                task
        );
    }



    // POST /api/tasks
    @PostMapping
    public ApiResponse<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO dto) {

        TaskResponseDTO created = service.createTask(dto);

        return new ApiResponse<>(
                true,
                "Task created successfully",
                created
        );
    }



    // PUT update
    @PutMapping("/{id}")
    public ApiResponse<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        TaskResponseDTO updated =
                service.updateTask(id, task);

        return new ApiResponse<>(
                true,
                "Task updated successfully",
                updated
        );
    }
    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);

        return new ApiResponse<>(
                true,
                "Task deleted successfully",
                null
        );
    }
}