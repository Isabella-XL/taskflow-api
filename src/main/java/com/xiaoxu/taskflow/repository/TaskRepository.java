package com.xiaoxu.taskflow.repository;

import com.xiaoxu.taskflow.entity.Task;
import com.xiaoxu.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUser(User user, Pageable pageable);
}