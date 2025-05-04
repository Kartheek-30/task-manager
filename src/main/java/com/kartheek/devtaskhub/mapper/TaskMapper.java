package com.kartheek.devtaskhub.mapper;

import com.kartheek.devtaskhub.dto.TaskRequestDTO;
import com.kartheek.devtaskhub.dto.TaskResponseDTO;
import com.kartheek.devtaskhub.model.Task;
import com.kartheek.devtaskhub.model.User;
import com.kartheek.devtaskhub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

public class TaskMapper {

    private final UserRepository userRepository;

    public TaskMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TaskResponseDTO returnTaskUsingDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : null,
                task.getAssignedUser() != null ? task.getAssignedUser().getUsername() : null
        );
    }

    public Task createTaskUsingDTO(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());

        if(dto.getAssignedUserId() != null) {
            User user = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedUser(user);
        }

        return task;
    }

    public void updateTaskEntity(Task task, TaskRequestDTO dto) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted((dto.isCompleted()));

        if (dto.getAssignedUserId() != null) {
            User user = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedUser(user);
        } else {
            task.setAssignedUser(null);
        }
    }

}
