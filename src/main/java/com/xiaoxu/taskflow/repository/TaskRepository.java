package com.xiaoxu.taskflow.repository;

import com.xiaoxu.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}