package com.example.controller;

import com.example.data.Task;
import com.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository repository;

    @GetMapping("{id}")
    public Optional<Task> find(@PathVariable("id") long id) {
        return repository.findById(id);
    }

    @GetMapping("/status/{status}")
    public List<Task> findByStatus(@PathVariable("status") Task.Status status) {
        return repository.findByStatus(status);
    }

    @PostMapping
    public Task create(@RequestBody CreateTaskInput input) {
        return repository.create(input.subject());
    }

    @PutMapping("/{id}")
    public Optional<Task> updateStatus(@PathVariable("id") long id,
                                       @RequestBody UpdateTaskInput input) {
        return repository.updateStatus(id, input.status());
    }

    private record CreateTaskInput(String subject) {}
    private record UpdateTaskInput(Task.Status status) {}
}
