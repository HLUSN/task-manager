package com.example.task_manager_sqlite;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for handling API requests for Task entities.
 * This class provides endpoints for CRUD (Create, Read, Update, Delete) operations on tasks,
 * returning JSON responses.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Retrieves a list of all tasks.
     * <p>
     * This method handles HTTP GET requests to the base API endpoint `/api/tasks`.
     * It fetches all task entities from the database and returns them as a JSON array.
     *
     * @return a list of all {@link Task} objects.
     */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves a single task by its ID.
     * <p>
     * This method handles HTTP GET requests to `/api/tasks/{id}`. It attempts to find a
     * task with the specified ID. If found, it returns the task object with a 200 OK status.
     * If not found, it returns a 404 Not Found status.
     *
     * @param id The ID of the task to retrieve.
     * @return a {@link ResponseEntity} containing the {@link Task} object if found, or a 404 Not Found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new task.
     * <p>
     * This method handles HTTP POST requests to `/api/tasks`. It takes a {@link Task} object
     * from the request body, saves it to the database, and returns the newly created task
     * with a 201 Created status. The `@Valid` annotation ensures the incoming task data
     * conforms to the validation rules defined in the {@link Task} class.
     *
     * @param task The task object to be created.
     * @return the newly created {@link Task} object.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task.
     * <p>
     * This method handles HTTP PUT requests to `/api/tasks/{id}`. It finds the existing
     * task by its ID and updates its name with the value from the request body. The `@Valid`
     * annotation ensures the updated task data is valid before saving. If the task is found,
     * it saves the updated task and returns it with a 200 OK status. If not found,
     * it returns a 404 Not Found status.
     *
     * @param id The ID of the task to update.
     * @param updatedTask The updated task object.
     * @return a {@link ResponseEntity} containing the updated {@link Task} object if the task exists, or a 404 Not Found status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task updatedTask) {
        Optional<Task> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setName(updatedTask.getName());
            return ResponseEntity.ok(taskRepository.save(existingTask));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a task by its ID.
     * <p>
     * This method handles HTTP DELETE requests to `/api/tasks/{id}`. It first checks if a task
     * with the given ID exists. If it does, the task is deleted and a 204 No Content status
     * is returned. If not, a 404 Not Found status is returned.
     *
     * @param id The ID of the task to delete.
     * @return a {@link ResponseEntity} with a 204 No Content status if the deletion was successful, or a 404 Not Found status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles validation exceptions for REST endpoints.
     * This method is automatically called when a `@Valid` object fails validation,
     * allowing for a custom response with a 400 Bad Request status and detailed error messages.
     *
     * @param ex The exception thrown by the validation process.
     * @return a map of field names to their corresponding error messages.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
