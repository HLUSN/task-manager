package com.example.task_manager_sqlite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    TaskRepository repository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        repository.deleteAll();
    }

    @Test
    void saveAndFind() {
        Task t = repository.save(new Task("Sample"));
        assertThat(t.getId()).isNotNull();
        assertThat(repository.findById(t.getId())).isPresent();
    }

    @Test
    void testFindByName() {
        // Save multiple tasks
        repository.save(new Task("Test Task"));
        repository.save(new Task("Another Task"));
        repository.save(new Task("Test Task")); // Duplicate name

        List<Task> foundTasks = repository.findByName("Test Task");
        assertThat(foundTasks).hasSize(2);
        assertThat(foundTasks.get(0).getName()).isEqualTo("Test Task");
        assertThat(foundTasks.get(1).getName()).isEqualTo("Test Task");
    }

    @Test
    void testDelete() {
        Task task = repository.save(new Task("To be deleted"));
        assertThat(repository.existsById(task.getId())).isTrue();

        repository.deleteById(task.getId());
        assertThat(repository.existsById(task.getId())).isFalse();
    }

    @Test
    void testFindAll() {
        repository.save(new Task("Task 1"));
        repository.save(new Task("Task 2"));

        List<Task> allTasks = repository.findAll();
        assertThat(allTasks).hasSize(2);
    }

    @Test
    void testFindNonExistentTask() {
        Optional<Task> task = repository.findById(999L);
        assertThat(task).isEmpty();
    }
}