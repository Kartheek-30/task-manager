package com.kartheek.devtaskhub.service;

import com.kartheek.devtaskhub.dto.TaskRequestDTO;
import com.kartheek.devtaskhub.dto.TaskResponseDTO;
import com.kartheek.devtaskhub.mapper.TaskMapper;
import com.kartheek.devtaskhub.model.Task;
import com.kartheek.devtaskhub.model.User;
import com.kartheek.devtaskhub.repository.TaskRepository;
import com.kartheek.devtaskhub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepo;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepo, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepo = userRepo;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponseDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(taskMapper::returnTaskUsingDTO)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponseDTO> getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::returnTaskUsingDTO);
    }

    public List<TaskResponseDTO> getTaskByUser(Long userId) {
        List<Task> taskByUser = taskRepository.findByAssignedUserId(userId);

        return taskByUser.stream()
                .map(taskMapper::returnTaskUsingDTO)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getUnassignedTasks() {
        List<Task> unassignedTasks = taskRepository.findByAssignedUserIsNull();

        return unassignedTasks.stream()
                .map(taskMapper::returnTaskUsingDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO createTask(
            TaskRequestDTO dto,
            Long createdByUserId,
            Long assignedToUserId
    ) {
        Task task = taskMapper.createTaskUsingDTO(dto);

        User creator = userRepo.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        task.setCreatedBy(creator);

        if(assignedToUserId != null) {
            User assignee = userService.getUserById(assignedToUserId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignedUser(assignee);
        }
        Task savedTask = taskRepository.save(task);
        return taskMapper.returnTaskUsingDTO(savedTask);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long taskId, @Valid TaskRequestDTO requestDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task Not Found"));

        taskMapper.updateTaskEntity(existingTask, requestDTO);

        Task savedTask = taskRepository.save(existingTask);
        return taskMapper.returnTaskUsingDTO(savedTask);
    }

    public TaskResponseDTO assignTaskToUser(Long taskId, Long userId) throws IllegalAccessException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (task.getAssignedUser() != null && task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalAccessException("Task is already assigned to this user");
        }

        task.setAssignedUser(user);

        Task updatedTask = taskRepository.save(task);
        return taskMapper.returnTaskUsingDTO(updatedTask);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
