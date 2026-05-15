package com.xiaoxu.taskflow.repository;

import com.xiaoxu.taskflow.entity.Task;
import com.xiaoxu.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);
}