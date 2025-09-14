package com.example.task_manager_sqlite;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByName(String name);  // safe method to avoid SQL injection
}
