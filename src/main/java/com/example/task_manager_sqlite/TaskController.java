package com.example.task_manager_sqlite;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    // Define constant instead of duplicating this literal "redirect:/tasks" 3 times
    private static final String REDIRECT_TASKS = "redirect:/tasks";

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping({"/", "/tasks"})
    public String index(Model model) {
        model.addAttribute("newTask", new Task());
        model.addAttribute("tasks", service.findAll());
        return "index";
    }

    @PostMapping("/tasks")
    public String addTask(@ModelAttribute("newTask") @Valid Task newTask,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tasks", service.findAll());
            return "index";
        }
        service.add(newTask.getName());
        return REDIRECT_TASKS; // Using constant instead of literal
    }

    @PostMapping("/tasks/{id}/update")
    public String updateTask(@PathVariable Long id, @RequestParam String name) {
        service.update(id, name);
        return REDIRECT_TASKS; // Using constant instead of literal
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        service.delete(id);
        return REDIRECT_TASKS; // Using constant instead of literal
    }
}