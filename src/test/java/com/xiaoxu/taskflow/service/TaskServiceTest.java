package com.xiaoxu.taskflow.service;

import com.xiaoxu.taskflow.dto.TaskRequestDTO;
import com.xiaoxu.taskflow.dto.TaskResponseDTO;
import com.xiaoxu.taskflow.entity.Role;
import com.xiaoxu.taskflow.entity.Task;
import com.xiaoxu.taskflow.entity.TaskStatus;
import com.xiaoxu.taskflow.entity.User;
import com.xiaoxu.taskflow.exception.ResourceNotFoundException;
import com.xiaoxu.taskflow.repository.TaskRepository;
import com.xiaoxu.taskflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Learn Testing");
        dto.setDescription("Mockito");
        dto.setStatus(TaskStatus.TODO);

        Authentication authentication =
                mock(Authentication.class);

        SecurityContext securityContext =
                mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("john");

        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Learn Testing");
        savedTask.setDescription("Mockito");
        savedTask.setStatus(TaskStatus.TODO);
        savedTask.setUser(user);

        when(repository.save(any(Task.class)))
                .thenReturn(savedTask);

        TaskResponseDTO response =
                taskService.createTask(dto);

        assertEquals(
                "Learn Testing",
                response.getTitle()
        );

        verify(userRepository)
                .findByUsername("john");

        verify(repository)
                .save(any(Task.class));
    }


    @Test
    void shouldReturnTaskById() {

        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        // Act
        TaskResponseDTO result = taskService.getTaskById(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {

        // Arrange
        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTaskById(1L)
        );

        verify(repository).findById(1L);
    }

    @Test
    void shouldUpdateTask_whenUserIsOwner() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        Task existingTask = new Task();
        existingTask.setId(10L);
        existingTask.setUser(user);

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Desc");
        updatedTask.setStatus(TaskStatus.DONE);

        when(repository.findById(10L))
                .thenReturn(Optional.of(existingTask));

        when(repository.save(any(Task.class)))
                .thenReturn(existingTask);

        // mock current user (IMPORTANT)
        mockCurrentUser(user);

        // Act
        TaskResponseDTO result =
                taskService.updateTask(10L, updatedTask);

        // Assert
        assertEquals("New Title", result.getTitle());

        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldThrow_whenUserNotOwnerAndNotAdmin() {

        // Arrange
        User owner = new User();
        owner.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(Role.USER);

        Task task = new Task();
        task.setId(10L);
        task.setUser(owner);

        when(repository.findById(10L))
                .thenReturn(Optional.of(task));

        mockCurrentUser(otherUser);

        Task updatedTask = new Task();

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(10L, updatedTask));

        verify(repository, never()).save(any());
    }
    @Test
    void adminShouldUpdateAnyTask() {

        // Arrange
        User admin = new User();
        admin.setId(99L);
        admin.setRole(Role.ADMIN);

        User owner = new User();
        owner.setId(1L);

        Task task = new Task();
        task.setId(10L);
        task.setUser(owner);

        Task updatedTask = new Task();
        updatedTask.setTitle("Admin Update");

        when(repository.findById(10L))
                .thenReturn(Optional.of(task));

        when(repository.save(any(Task.class)))
                .thenReturn(task);

        mockCurrentUser(admin);

        // Act
        TaskResponseDTO result =
                taskService.updateTask(10L, updatedTask);

        // Assert
        assertEquals("Admin Update", result.getTitle());
    }

    @Test
    void shouldDeleteTask_whenUserIsOwner() {

        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        Task task = new Task();
        task.setId(10L);
        task.setUser(user);

        when(repository.findById(10L))
                .thenReturn(Optional.of(task));

        mockCurrentUser(user);

        taskService.deleteTask(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    void shouldThrow_whenUserNotOwner() {

        User owner = new User();
        owner.setId(1L);

        User attacker = new User();
        attacker.setId(2L);
        attacker.setRole(Role.USER);

        Task task = new Task();
        task.setId(10L);
        task.setUser(owner);

        when(repository.findById(10L))
                .thenReturn(Optional.of(task));

        mockCurrentUser(attacker);

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(10L));

        verify(repository, never()).deleteById(any());
    }

    @Test
    void adminShouldDeleteAnyTask() {

        User admin = new User();
        admin.setId(99L);
        admin.setRole(Role.ADMIN);

        User owner = new User();
        owner.setId(1L);

        Task task = new Task();
        task.setId(10L);
        task.setUser(owner);

        when(repository.findById(10L))
                .thenReturn(Optional.of(task));

        mockCurrentUser(admin);

        taskService.deleteTask(10L);

        verify(repository).deleteById(10L);
    }

    private void mockCurrentUser(User user) {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn(user.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
    }
}