package com.xiaoxu.taskflow.service;

import com.xiaoxu.taskflow.entity.*;
import com.xiaoxu.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.xiaoxu.taskflow.dto.*;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskResponseDTO> getAllTasks() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Create a task
    public TaskResponseDTO createTask(TaskRequestDTO dto) {

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));

        Task saved = repository.save(task);

        return mapToDTO(saved);
    }

    private TaskResponseDTO mapToDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        return dto;
    }


    public Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());

        return repository.save(task);
    }

    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Task not found: " + id);
        }
        repository.deleteById(id);
    }


}