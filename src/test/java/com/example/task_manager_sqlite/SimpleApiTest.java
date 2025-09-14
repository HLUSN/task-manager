package com.example.task_manager_sqlite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllTasks() {
        // API Test 1: GET all tasks
        String url = "http://localhost:" + port + "/api/tasks";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("["); // Should return JSON array
    }

    @Test
    public void testCreateTask() {
        // API Test 2: POST create task
        String url = "http://localhost:" + port + "/api/tasks";
        Task newTask = new Task("API Test Task");

        ResponseEntity<Task> response = restTemplate.postForEntity(url, newTask, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("API Test Task");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    public void testGetTaskById() {
        // First create a task
        Task newTask = new Task("Test Task for Get");
        ResponseEntity<Task> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/tasks", newTask, Task.class);

        // Then try to get it by ID
        Long taskId = createResponse.getBody().getId();
        ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/tasks/" + taskId, Task.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getName()).isEqualTo("Test Task for Get");
    }
}