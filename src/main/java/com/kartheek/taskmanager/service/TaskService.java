package com.kartheek.taskmanager.service;

import com.kartheek.taskmanager.model.Task;
import com.kartheek.taskmanager.model.User;
import com.kartheek.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repo;
    private final UserService userService;

    public TaskService(TaskRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public List<Task> getAllTasks() {
        return repo.findAll();
    }

    public Optional<Task> getTaskById(Long taskId) {
        return repo.findById(taskId);
    }

    public Task createTask(Task task, Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        return repo.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, Task newTask) {
        return repo.findById(taskId).map(task -> {
            task.setTitle(newTask.getTitle());
            task.setDescription(newTask.getDescription());
            task.setCompleted(newTask.isCompleted());

            if(newTask.getUser() != null && newTask.getUser().getId() != null) {
                User user = userService.getUserById(newTask.getUser().getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                task.setUser(user);
            }
            return repo.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(Long taskId) {
        repo.deleteById(taskId);
    }
}
