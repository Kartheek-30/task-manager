package com.kartheek.devtaskhub.controller;

import com.kartheek.devtaskhub.dto.TaskRequestDTO;
import com.kartheek.devtaskhub.dto.TaskResponseDTO;
import com.kartheek.devtaskhub.error.ErrorResponse;
import com.kartheek.devtaskhub.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId
    ) {
        if("DEVELOPER".equalsIgnoreCase(role)) {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required for DEVELOPER role");
            }
            return taskService.getTaskByUser(userId);
        }
        return taskService.getAllTasks();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long taskId
    ) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/tasks")
    public List<TaskResponseDTO> getTaskByUser(
            @PathVariable Long userId
    ) {
        return taskService.getTaskByUser(userId);
    }

    @GetMapping("/unassigned")
    public List<TaskResponseDTO> getUnassignedTasks() {
        return taskService.getUnassignedTasks();
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO requestDTO,
            @RequestParam Long createdByUserId,
            @RequestParam(required = false) Long assignedToUserId
    ) {
        TaskResponseDTO responseDTO = taskService.createTask(requestDTO, createdByUserId, assignedToUserId);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequestDTO requestDTO
    ) {
        try {
            TaskResponseDTO updatedTaskDTO = taskService.updateTask(taskId, requestDTO);
            return ResponseEntity.ok(updatedTaskDTO);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTaskToUser(
            @PathVariable Long taskId,
            @RequestParam Long userId
    ) {
        try {
            TaskResponseDTO updatedTaskDto = taskService.assignTaskToUser(taskId, userId);
            return ResponseEntity.ok(updatedTaskDto);
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid access: " + e.getMessage()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId
    ) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
