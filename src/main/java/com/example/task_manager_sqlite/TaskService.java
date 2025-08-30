package com.example.task_manager_sqlite;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() { return repository.findAll(); }

    public Task add(String name) { return repository.save(new Task(name)); }

    public Task update(Long id, String name) {
        Task t = repository.findById(id).orElseThrow();
        t.setName(name);
        return repository.save(t);
    }

    public void delete(Long id) { repository.deleteById(id); }
}
