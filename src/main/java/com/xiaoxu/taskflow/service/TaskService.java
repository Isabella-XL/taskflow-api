package com.xiaoxu.taskflow.service;

import com.xiaoxu.taskflow.entity.*;
import com.xiaoxu.taskflow.exception.ResourceNotFoundException;
import com.xiaoxu.taskflow.repository.TaskRepository;
import com.xiaoxu.taskflow.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.xiaoxu.taskflow.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private TaskResponseDTO convertToDTO(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(String.valueOf(task.getStatus()));

        return dto;
    }


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {

        User user = getCurrentUser();

        List<String> allowedFields = List.of("id", "title", "status", "createdAt");

        // ✅ 1. validate sort first
        Sort sort = pageable.getSort();

        boolean invalidSort = sort.stream()
                .anyMatch(order -> !allowedFields.contains(order.getProperty()));

        if (invalidSort || sort.isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("id")
            );
        }

        // ✅ 2. business logic
        Page<Task> tasks;

        if (user.getRole() == Role.ADMIN) {
            tasks = repository.findAll(pageable);
        } else {
            tasks = repository.findByUser(user, pageable);
        }

        return tasks.map(this::convertToDTO);
    }

    // Create a task
    public TaskResponseDTO createTask(TaskRequestDTO dto) {

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());

        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        task.setUser(user);

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


    public TaskResponseDTO getTaskById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return convertToDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, Task updatedTask) {

        User currentUser = getCurrentUser();
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // ownership check
        if (currentUser.getRole() != Role.ADMIN &&
                !task.getUser().getId().equals(currentUser.getId())) {

            throw new ResourceNotFoundException("You are not allowed to update this task");
        }


        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());

        Task saved = repository.save(task);

        return convertToDTO(saved);
    }

    public void deleteTask(Long id) {

        User currentUser = getCurrentUser();

        Task task = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found: " + id));

        // ownership protection
        if (currentUser.getRole() != Role.ADMIN &&
                !task.getUser().getId().equals(currentUser.getId())) {

            throw new ResourceNotFoundException("You are not allowed to delete this task");
        }

        repository.deleteById(id);
    }

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        return authentication.getName();
    }

}