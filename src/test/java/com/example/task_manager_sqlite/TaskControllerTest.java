package com.example.task_manager_sqlite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository repository;

    @Test
    void addUpdateDeleteFlow() throws Exception {
        // Clear all tasks before the test
        repository.deleteAll();

        // 1. Check if the initial tasks page is accessible
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("tasks", org.hamcrest.Matchers.empty()));

        // 2. Add a new task using a POST request
        mockMvc.perform(post("/tasks")
                        .param("name", "Write tests"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Verify the task was added
        assertThat(repository.findAll()).hasSize(1);
        Task addedTask = repository.findAll().get(0);
        assertThat(addedTask.getName()).isEqualTo("Write tests");

        // 3. Update the task using a POST request
        mockMvc.perform(post("/tasks/{id}/update", addedTask.getId())
                        .param("name", "Write better tests"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Verify the task was updated
        Task updatedTask = repository.findById(addedTask.getId()).orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getName()).isEqualTo("Write better tests");

        // 4. Delete the task using a POST request
        mockMvc.perform(post("/tasks/{id}/delete", updatedTask.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Verify the task was deleted
        assertThat(repository.findAll()).hasSize(0);
    }

    @Test
    void testAddTaskWithEmptyName() throws Exception {
        mockMvc.perform(post("/tasks")
                        .param("name", ""))
                .andExpect(status().isOk()) // Should not redirect due to validation error
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("newTask", "name"));
    }
}